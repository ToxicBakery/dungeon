package com.toxicbakery.game.dungeon.machine.init

import com.toxicbakery.game.dungeon.machine.ProcessorMachine
import com.toxicbakery.game.dungeon.machine.authentication.AuthenticationMachine
import com.toxicbakery.game.dungeon.machine.registration.RegistrationMachine
import com.toxicbakery.game.dungeon.model.session.GameSession
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

/**
 * Main menu providing basic information and requesting the user to either authenticate or register.
 */
private class InitMachineImpl(
    private val authenticationMachine: AuthenticationMachine,
    private val registrationMachine: RegistrationMachine
) : InitMachine {

    override val name: String = "InitMachine"

    override val currentState: InitState = InitState.Init

    override suspend fun acceptMessage(
        gameSession: GameSession,
        message: String
    ): ProcessorMachine<*> = when (message) {
        CMD_LOGIN -> authenticationMachine
        CMD_REGISTER -> registrationMachine
        else -> this
    }

    override suspend fun initMachine(gameSession: GameSession): ProcessorMachine<InitState> {
        gameSession.printInstructions()
        return this
    }

    private suspend fun GameSession.printInstructions() = sendMessage(HELP_INSTRUCTIONS)

    companion object {
        private const val CMD_LOGIN = "login"
        private const val CMD_REGISTER = "register"
        private const val HELP_INSTRUCTIONS = "Available options:\n$CMD_LOGIN\n$CMD_REGISTER"
    }
}

interface InitMachine : ProcessorMachine<InitState>

val initMachineModule = Kodein.Module("initMachineModule") {
    bind<InitMachine>() with provider {
        InitMachineImpl(
            authenticationMachine = instance(),
            registrationMachine = instance()
        )
    }
}
