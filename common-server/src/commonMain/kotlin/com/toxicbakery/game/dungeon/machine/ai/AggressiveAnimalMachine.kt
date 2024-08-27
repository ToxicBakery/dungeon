package com.toxicbakery.game.dungeon.machine.ai

import com.toxicbakery.game.dungeon.defaults.BaseAnimal.Wolf
import com.toxicbakery.game.dungeon.machine.TickableMachine
import com.toxicbakery.game.dungeon.manager.*
import com.toxicbakery.game.dungeon.map.MapLegend
import com.toxicbakery.game.dungeon.map.model.Direction
import com.toxicbakery.game.dungeon.model.Lookable
import com.toxicbakery.game.dungeon.model.Lookable.Animal
import com.toxicbakery.game.dungeon.model.Lookable.Player
import com.toxicbakery.game.dungeon.model.character.stats.Stats
import com.toxicbakery.game.dungeon.model.world.LookLocation
import com.toxicbakery.game.dungeon.util.DiceRoll
import com.toxicbakery.game.dungeon.util.getRandom
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import kotlin.jvm.JvmStatic

interface AggressiveAnimalMachine : TickableMachine<AIState>

private data class AggressiveAnimalMachineImpl(
    private val diceRoll: DiceRoll,
    private val state: AggressiveAiState,
    private val playerManager: PlayerManager,
    private val lookManager: LookManager,
    private val communicationManager: CommunicationManager,
) : AggressiveAnimalMachine {

    override val name: String = "AggressiveAnimalMachine (${state.subject.name})"
    override val currentState: AIState = state.aiState
    override val instanceId: String = state.subject.id

    override suspend fun tick(): TickableMachine<AIState> = when {
        state.subject.isDead && currentState != AIState.DECEASED -> die()
        currentState == AIState.FIGHTING -> tickFight()
        currentState == AIState.FLEEING -> tickFlee()
        currentState == AIState.WANDERING -> pivot()
        currentState == AIState.DECEASED -> tickDie()
        else -> this
    }

    private fun tickFight(): TickableMachine<AIState> =
        if (diceRoll.roll(CHANCE_FLEE_WHEN_ATTACKED)) {
            // TODO Evaluate fight, if winning potentially winning, don't run
            copy(
                state = state.copy(
                    aiState = AIState.FLEEING
                )
            )
        } else this

    private fun tickFlee(): TickableMachine<AIState> =
        if (diceRoll.roll(CHANCE_STOP_FLEE)) {
            copy(
                state = state.copy(
                    aiState = AIState.WANDERING
                )
            )
        } else this

    private suspend fun pivot(): TickableMachine<AIState> = when {
        diceRoll.roll(CHANCE_ATTACK) -> tickAttack()
        diceRoll.roll(CHANCE_MOVE_WHILE_WANDERING) -> tickWandering()
        else -> this
    }

    private suspend fun tickWandering(): TickableMachine<AIState> {
        val randomDirection = Direction.getRandomDirection()
        val walkTarget = lookManager.look(
            lookable = state.subject,
            direction = randomDirection
        )
        if (walkTarget.canBeWalkedTo) {
            val updatedState = state.copy(
                subject = state.subject.copy(
                    location = walkTarget.location
                )
            )
            state.npcManager.updateNpc(updatedState.subject)

            communicationManager.notifyPlayersAtLocation(
                message = "A ${state.subject.name} departs to the ${randomDirection.name.lowercase()}",
                location = state.subject.location,
            )
            communicationManager.notifyPlayersAtLocation(
                message = "A ${state.subject.name} arrives from ${randomDirection.sourceDirection.name.lowercase()}",
                location = walkTarget.location,
            )
            return copy(state = updatedState)
        }

        return this
    }

    private suspend fun tickAttack(): TickableMachine<AIState> {
        val subject = state.subject
        val here = lookManager.look(
            lookable = subject,
            direction = null,
        )

        suspend fun performAttack(target: Lookable): TickableMachine<AIState> {
            // Attack and switch to fight
            val attack = Stats(health = -10) // TODO attack based on stats
            return when (target) {
                is Player -> attackPlayer(subject, target, attack)
                is Animal -> {
                    state.npcManager.updateNpc(
                        target.copy(
                            stats = target.stats + attack
                        )
                    )
                    this
                }

                else -> this
            }
        }

        return state.target
            ?.let { target -> performAttack(target) }
            ?: here.lookables
                .filter { it !is Animal || it.name != Wolf.displayName }
                .getRandom()
                ?.let { target ->
                    performAttack(target)

                    // Lock onto target
                    copy(
                        state = state.copy(
                            target = target,
                        ),
                    )
                }
            ?: this
    }

    private suspend fun attackPlayer(
        subject: Animal,
        target: Player,
        attack: Stats,
    ): TickableMachine<AIState> {
        communicationManager.notify(target, "The ${subject.name} lunges at you")
        playerManager.updatePlayer(
            target.copy(
                stats = target.stats + attack
            )
        )

        return if (target.isDead) {
            communicationManager.serverMessage("${target.name} has been vanquished by a ${subject.name}.", target)

            copy(
                state = state.copy(
                    target = null
                )
            )
        } else this
    }

    private suspend fun attackAnimal(
        subject: Animal,
        target: Animal,
        attack: Stats,
    ) {
        // TODO notify subject state machine of attack
        state.npcManager.updateNpc(
            target.copy(
                stats = target.stats + attack
            )
        )
    }

    private fun tickDie(): TickableMachine<AIState> {
        return this
    }

    private fun die(): TickableMachine<AIState> = copy(
        state = state.copy(
            aiState = AIState.DECEASED
        )
    )

    companion object {
        private const val CHANCE_ATTACK = 100
        private const val CHANCE_MOVE_WHILE_WANDERING = 500
        private const val CHANCE_FLEE_WHEN_ATTACKED = 50
        private const val CHANCE_STOP_FLEE = 100

        @JvmStatic
        private val walkableMapLegends = setOf(
            MapLegend.FOREST_1.byteRepresentation,
            MapLegend.FOREST_2.byteRepresentation,
            MapLegend.FOREST_3.byteRepresentation,
            MapLegend.FOREST_4.byteRepresentation,
            MapLegend.DESERT.byteRepresentation,
            MapLegend.PLAIN.byteRepresentation,
            MapLegend.BEACH.byteRepresentation,
            MapLegend.MOUNTAIN.byteRepresentation,
        )

        @JvmStatic
        private val LookLocation.canBeWalkedTo
            get() = walkableMapLegends.contains(mapLegendByte)
    }
}

private data class AggressiveAiState(
    val aiState: AIState = AIState.WANDERING,
    val subject: Animal,
    val npcManager: NpcManager,
    val target: Lookable? = null,
)

val aggressiveAnimalMachineModule = DI.Module("aggressiveAnimalMachineModule") {
    bind<AggressiveAnimalMachine>() with factory { animalInit: AnimalInit ->
        AggressiveAnimalMachineImpl(
            diceRoll = instance(),
            state = AggressiveAiState(
                subject = animalInit.animal,
                npcManager = animalInit.npcManager,
            ),
            lookManager = instance(),
            playerManager = instance(),
            communicationManager = instance(),
        )
    }
}
