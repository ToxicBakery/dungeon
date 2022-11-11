package com.toxicbakery.game.dungeon.machine.ai

import com.toxicbakery.game.dungeon.machine.Machine
import com.toxicbakery.game.dungeon.machine.TickableMachine
import com.toxicbakery.game.dungeon.model.Lookable.Animal
import com.toxicbakery.game.dungeon.util.DiceRoll
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.factory
import org.kodein.di.erased.instance

private data class PassiveAnimalMachineImpl(
    private val animal: Animal,
    private val passiveAiState: PassiveAiState = PassiveAiState(),
    private val diceRoll: DiceRoll,
) : PassiveAnimalMachine {

    override val name: String = "PassiveAnimalMachine"
    override val currentState: AIState = passiveAiState.state

    override fun tick(): PassiveAnimalMachine {
        return when (currentState) {
            AIState.FIGHTING -> this
            AIState.FLEEING -> this
            AIState.WANDERING -> tickWandering()
        }
    }

    fun tickWandering(): PassiveAnimalMachine {
        if (diceRoll.roll(CHANCE_MOVE_WHILE_WANDERING)) {
            // TODO Create manager to move creatures
            // TODO Create global tick watcher to move TickableMachine
            animal.location
        }

        return this
    }

    companion object {
        private const val CHANCE_MOVE_WHILE_WANDERING = 20
    }
}

interface PassiveAnimalMachine : Machine<AIState>, TickableMachine<AIState>

private data class PassiveAiState(
    val state: AIState = AIState.WANDERING
)

val passiveAnimalMachineModule = Kodein.Module("passiveAnimalMachineModule") {
    bind<PassiveAnimalMachine>() with factory { animal: Animal ->
        PassiveAnimalMachineImpl(
            animal = animal,
            diceRoll = instance()
        )
    }
}
