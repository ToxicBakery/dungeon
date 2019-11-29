package com.toxicbakery.game.dungeon.machine.registration

import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import com.toxicbakery.game.dungeon.auth.Credentials
import com.toxicbakery.game.dungeon.exception.AlreadyRegisteredException
import com.toxicbakery.game.dungeon.machine.Machine
import com.toxicbakery.game.dungeon.machine.authentication.AuthenticationMachine
import com.toxicbakery.game.dungeon.manager.AuthenticationManager
import com.toxicbakery.game.dungeon.model.session.GameSession
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.factory
import org.kodein.di.erased.instance

private class RegistrationMachineImpl(
    private val authenticationManager: AuthenticationManager,
    private val authenticationMachineFactory: (GameSession) -> AuthenticationMachine,
    private val gameSession: GameSession
) : RegistrationMachine {

    private val _currentState: AtomicReference<RegistrationState> = AtomicReference(RegistrationState.Init)
    private var credentials: Credentials = Credentials()

    override val name: String = "RegistrationMachine"

    override val currentState: RegistrationState
        get() = _currentState.value

    override suspend fun acceptMessage(message: String): Machine<*> {
        _currentState.value = cycle(message)
        return when (currentState) {
            RegistrationState.Registered -> authenticationMachineFactory(gameSession)
            else -> this
        }
    }

    override suspend fun initMachine() {
        _currentState.value = initRegistration()
    }

    private suspend fun cycle(message: String): RegistrationState = when (_currentState.value) {
        RegistrationState.Init -> initRegistration()
        RegistrationState.AwaitingUsername -> takeUsernameAndProceed(message)
        RegistrationState.AwaitingPassword -> takePasswordAndProceed(message)
        RegistrationState.Registered -> error("Request to process on end state")
    }

    private suspend fun initRegistration(): RegistrationState {
        credentials = Credentials()
        gameSession.send("Choose a username:")
        return RegistrationState.AwaitingUsername
    }

    private suspend fun takeUsernameAndProceed(username: String): RegistrationState =
        if (username.isEmpty()) RegistrationState.Init
        else {
            credentials = credentials.copy(username = username)
            gameSession.send("Enter a password:")
            RegistrationState.AwaitingPassword
        }

    private suspend fun takePasswordAndProceed(password: String): RegistrationState =
        if (password.isEmpty()) RegistrationState.AwaitingPassword
        else {
            credentials = credentials.copy(password = password)
            try {
                authenticationManager.registerPlayer(credentials)
                gameSession.send("Registration successful! Returning to login.")
                RegistrationState.Registered
            } catch (e: AlreadyRegisteredException) {
                gameSession.send("Registration failure! User already exists.")
                initRegistration()
            }
        }

}

interface RegistrationMachine : Machine<RegistrationState>

val registrationMachineModule = Kodein.Module("registrationMachineModule") {
    bind<RegistrationMachine>() with factory { gameSession: GameSession ->
        RegistrationMachineImpl(
            authenticationManager = instance(),
            authenticationMachineFactory = factory(),
            gameSession = gameSession
        )
    }
}
