package com.toxicbakery.game.dungeon.machine.registration

import com.toxicbakery.game.dungeon.exception.AlreadyRegisteredException
import com.toxicbakery.game.dungeon.machine.ProcessorMachine
import com.toxicbakery.game.dungeon.machine.command.CommandMachine
import com.toxicbakery.game.dungeon.machine.command.processor.ProcessorLook
import com.toxicbakery.game.dungeon.manager.AuthenticationManager
import com.toxicbakery.game.dungeon.model.auth.Credentials
import com.toxicbakery.game.dungeon.model.client.ExpectedResponseType
import com.toxicbakery.game.dungeon.model.session.GameSession
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private data class RegistrationMachineImpl(
    private val commandMachine: CommandMachine,
    private val authenticationManager: AuthenticationManager,
    private val registrationMachineState: RegistrationMachineState = RegistrationMachineState()
) : RegistrationMachine {

    override val name: String = "RegistrationMachine"

    override val currentState: RegistrationState = registrationMachineState.state

    override suspend fun acceptMessage(
        gameSession: GameSession,
        message: String
    ): ProcessorMachine<*> = gameSession.cycle(message).let { nextState ->
        when (nextState.state) {
            RegistrationState.Registered -> commandMachine.acceptMessage(
                gameSession,
                ProcessorLook.COMMAND
            )

            else -> copy(registrationMachineState = nextState)
        }
    }

    override suspend fun initMachine(gameSession: GameSession) = when (currentState) {
        RegistrationState.Init -> copy(registrationMachineState = gameSession.cycle())
        else -> this
    }

    private suspend fun GameSession.cycle(message: String = ""): RegistrationMachineState =
        when (currentState) {
            RegistrationState.Init -> initRegistration()
            RegistrationState.AwaitingUsername -> takeUsernameAndProceed(message)
            RegistrationState.AwaitingPassword -> takePasswordAndProceed(message)
            RegistrationState.Registered -> error("Request to process on end state")
        }

    private suspend fun GameSession.initRegistration(): RegistrationMachineState {
        sendMessage("Choose a username:")
        return registrationMachineState.copy(state = RegistrationState.AwaitingUsername)
    }

    private suspend fun GameSession.takeUsernameAndProceed(username: String): RegistrationMachineState =
        if (username.isEmpty()) {
            registrationMachineState.copy(
                state = RegistrationState.Init,
                credentials = Credentials()
            )
        } else {
            sendMessage("Enter a password:", ExpectedResponseType.Secure)
            registrationMachineState.copy(
                state = RegistrationState.AwaitingPassword,
                credentials = registrationMachineState.credentials.copy(username = username)
            )
        }

    private suspend fun GameSession.takePasswordAndProceed(password: String): RegistrationMachineState =
        if (password.isEmpty()) registrationMachineState.copy(state = RegistrationState.AwaitingPassword)
        else {
            try {
                val newCredentials = registrationMachineState.credentials.copy(password = password)
                authenticationManager.registerPlayer(newCredentials)
                val player = authenticationManager.authenticatePlayer(newCredentials, this)
                sendMessage("Registration successful, welcome ${player.name}!")
                registrationMachineState.copy(
                    state = RegistrationState.Registered,
                    credentials = newCredentials
                )
            } catch (_: AlreadyRegisteredException) {
                sendMessage("Registration failure! User already exists.")
                initRegistration()
            }
        }
}

private data class RegistrationMachineState(
    val credentials: Credentials = Credentials(),
    val state: RegistrationState = RegistrationState.Init
)

interface RegistrationMachine : ProcessorMachine<RegistrationState>

val registrationMachineModule = Kodein.Module("registrationMachineModule") {
    bind<RegistrationMachine>() with provider {
        RegistrationMachineImpl(
            commandMachine = instance(),
            authenticationManager = instance(),
        )
    }
}
