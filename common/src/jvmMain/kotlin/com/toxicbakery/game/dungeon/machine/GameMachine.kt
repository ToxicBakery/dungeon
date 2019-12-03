package com.toxicbakery.game.dungeon.machine

import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import com.toxicbakery.game.dungeon.machine.init.InitMachine
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.logging.Arbor
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.factory

/**
 * Entry point into the game state machine.
 */
private class GameMachineImpl(
    initMachine: InitMachine
) : GameMachine {

    private val currentMachine: AtomicReference<Machine<*>> = AtomicReference(initMachine)

    override suspend fun initMachine() {
        currentMachine.value.initMachine()
    }

    override suspend fun receivedMessage(message: String) {
        val processingMachine = currentMachine.value
        val nextMachine = processingMachine.acceptMessage(message)
        Arbor.d(
            "Message transitioned machines from %s.%s to %s.%s",
            processingMachine.name,
            processingMachine.currentState,
            nextMachine.name,
            nextMachine.currentState
        )
        currentMachine.value = nextMachine
        if (processingMachine != nextMachine) nextMachine.initMachine()
    }

}

interface GameMachine {

    suspend fun initMachine()

    suspend fun receivedMessage(message: String)

}

val gameMachineModule = Kodein.Module("gameMachineModule") {
    bind<GameMachine>() with factory { gameSession: GameSession ->
        GameMachineImpl(
            initMachine = factory<GameSession, InitMachine>()(gameSession)
        )
    }
}
