package com.toxicbakery.game.dungeon.machine.ai

import com.toxicbakery.game.dungeon.gameProcessingDispatcher
import com.toxicbakery.game.dungeon.machine.Machine
import com.toxicbakery.game.dungeon.machine.TickableMachine
import com.toxicbakery.game.dungeon.manager.NpcManager
import com.toxicbakery.game.dungeon.manager.WorldManager
import com.toxicbakery.game.dungeon.map.MapLegend
import com.toxicbakery.game.dungeon.map.model.Direction
import com.toxicbakery.game.dungeon.model.Lookable.Animal
import com.toxicbakery.game.dungeon.model.world.LookLocation
import com.toxicbakery.game.dungeon.persistence.store.GameClock
import com.toxicbakery.game.dungeon.util.DiceRoll
import kotlin.jvm.JvmStatic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.factory
import org.kodein.di.erased.instance

private data class PassiveAnimalMachineImpl(
    private val diceRoll: DiceRoll,
    private val gameClock: GameClock,
    private val npcManager: NpcManager,
    private val worldManager: WorldManager,
    private val passiveAiState: PassiveAiState,
) : PassiveAnimalMachine {

    private val tickJob = CoroutineScope(gameProcessingDispatcher).launch {
        gameClock.gameTickFlow.collect { tick() }
    }

    override val name: String = "PassiveAnimalMachine"
    override val currentState: AIState = passiveAiState.state

    override suspend fun tick(): PassiveAnimalMachine = when {
        passiveAiState.animal.isDead && currentState != AIState.DECEASED -> die()
        currentState == AIState.FIGHTING -> tickFight()
        currentState == AIState.FLEEING -> tickFlee()
        currentState == AIState.WANDERING -> tickWandering()
        currentState == AIState.DECEASED -> tickDie()
        else -> this
    }

    private suspend fun tickFight(): PassiveAnimalMachine = this

    private suspend fun tickFlee(): PassiveAnimalMachine = this

    private suspend fun tickWandering(): PassiveAnimalMachine {
        if (diceRoll.roll(CHANCE_MOVE_WHILE_WANDERING)) {
            val walkTarget = worldManager.look(
                lookable = passiveAiState.animal,
                direction = Direction.getRandomDirection()
            )
            if (walkTarget.canBeWalkedTo) {
                val updatedState = passiveAiState.copy(
                    animal = passiveAiState.animal.copy(
                        location = walkTarget.location
                    )
                )
                npcManager.updateNpc(updatedState.animal)
                return copy(passiveAiState = updatedState)
            }
        }

        return this
    }

    private fun tickDie(): PassiveAnimalMachine {
        tickJob.cancel(DeadNpcCancellationException(passiveAiState.animal))
        return this
    }

    private fun die(): PassiveAnimalMachine = copy(
        passiveAiState = passiveAiState.copy(
            state = AIState.DECEASED
        )
    )

    companion object {
        private const val CHANCE_MOVE_WHILE_WANDERING = 20

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

interface PassiveAnimalMachine : Machine<AIState>, TickableMachine<AIState>

private data class PassiveAiState(
    val state: AIState = AIState.WANDERING,
    val animal: Animal
)

val passiveAnimalMachineModule = Kodein.Module("passiveAnimalMachineModule") {
    bind<PassiveAnimalMachine>() with factory { animal: Animal ->
        PassiveAnimalMachineImpl(
            diceRoll = instance(),
            gameClock = instance(),
            npcManager = instance(),
            worldManager = instance(),
            passiveAiState = PassiveAiState(
                animal = animal
            )
        )
    }
}
