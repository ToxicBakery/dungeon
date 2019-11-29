package com.toxicbakery.game.dungeon.machine.init

import com.toxicbakery.game.dungeon.machine.Machine
import com.toxicbakery.game.dungeon.machine.authentication.AuthenticationMachine
import com.toxicbakery.game.dungeon.machine.registration.RegistrationMachine
import com.toxicbakery.game.dungeon.model.session.GameSession
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.factory

/**
 * Main menu providing basic information and requesting the user to either authenticate or register.
 */
private class InitMachineImpl(
    private val authenticationMachineFactory: (GameSession) -> AuthenticationMachine,
    private val registrationMachineFactory: (GameSession) -> RegistrationMachine,
    private val gameSession: GameSession
) : InitMachine {

    override val name: String = "InitMachine"

    override val currentState: InitState = InitState.Init

    override suspend fun acceptMessage(message: String): Machine<*> = when (message) {
        CMD_LOGIN -> authenticationMachineFactory(gameSession)
        CMD_REGISTER -> registrationMachineFactory(gameSession)
        else -> {
            gameSession.send("Invalid request")
            printInstructions()
            this
        }
    }

    override suspend fun initMachine() = printInstructions()

    private suspend fun printInstructions() = gameSession.send(HELP_INSTRUCTIONS)

    companion object {
        private const val CMD_LOGIN = "login"
        private const val CMD_REGISTER = "register"
        private const val HELP_INSTRUCTIONS = "Available options:\n$CMD_LOGIN\n$CMD_REGISTER"
    }

}

interface InitMachine : Machine<InitState>

val initMachineModule = Kodein.Module("initMachineModule") {
    bind<InitMachine>() with factory { gameSession: GameSession ->
        InitMachineImpl(
            authenticationMachineFactory = factory(),
            registrationMachineFactory = factory(),
            gameSession = gameSession
        )
    }
}
