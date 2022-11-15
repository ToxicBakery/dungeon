package com.toxicbakery.game.dungeon.machine.ai

import com.toxicbakery.game.dungeon.machine.TickableMachine
import com.toxicbakery.game.dungeon.manager.CommunicationManager
import com.toxicbakery.game.dungeon.manager.LookManager
import com.toxicbakery.game.dungeon.manager.PlayerManager
import com.toxicbakery.game.dungeon.map.MapLegend
import com.toxicbakery.game.dungeon.map.model.Direction
import com.toxicbakery.game.dungeon.model.Lookable.Animal
import com.toxicbakery.game.dungeon.model.world.LookLocation
import com.toxicbakery.game.dungeon.persistence.npc.NpcDatabase
import com.toxicbakery.game.dungeon.util.DiceRoll
import kotlin.jvm.JvmStatic
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance

private data class PassiveAnimalMachineImpl(
    private val diceRoll: DiceRoll,
    private val state: PassiveAiState,
    private val npcDatabase: NpcDatabase,
    private val playerManager: PlayerManager,
    private val lookManager: LookManager,
    private val communicationManager: CommunicationManager,
) : TickableMachine<AIState> {

    override val name: String = "PassiveAnimalMachine (${state.subject.name})"
    override val currentState: AIState = state.aiState
    override val instanceId: String = state.subject.id

    override suspend fun tick(): TickableMachine<AIState> = when {
        state.subject.isDead && currentState != AIState.DECEASED -> die()
        currentState == AIState.FIGHTING -> tickFight()
        currentState == AIState.FLEEING -> tickFlee()
        currentState == AIState.WANDERING -> tickWandering()
        currentState == AIState.DECEASED -> tickDie()
        else -> this
    }

    private fun tickFight(): TickableMachine<AIState> =
        if (diceRoll.roll(CHANCE_FLEE_WHEN_ATTACKED)) {
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

    private suspend fun tickWandering(): TickableMachine<AIState> {
        if (diceRoll.roll(CHANCE_MOVE_WHILE_WANDERING)) {
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
                npcDatabase.updateNpc(updatedState.subject)

                communicationManager.notifyPlayersAtLocation(
                    message = "${state.subject.name} departs to the ${randomDirection.name.lowercase()}",
                    location = state.subject.location,
                )
                communicationManager.notifyPlayersAtLocation(
                    message = "${state.subject.name} arrives from ${randomDirection.sourceDirection.name.lowercase()}",
                    location = walkTarget.location,
                )
                return copy(state = updatedState)
            }
        }

        return this
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

private data class PassiveAiState(
    val aiState: AIState = AIState.WANDERING,
    val subject: Animal
)

val passiveAnimalMachineModule = DI.Module("passiveAnimalMachineModule") {
    bind<TickableMachine<*>>() with factory { animal: Animal ->
        PassiveAnimalMachineImpl(
            diceRoll = instance(),
            state = PassiveAiState(
                subject = animal
            ),
            npcDatabase = instance(),
            lookManager = instance(),
            playerManager = instance(),
            communicationManager = instance(),
        )
    }
}
