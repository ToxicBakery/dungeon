package com.toxicbakery.game.dungeon.machine.authentication

import co.touchlab.stately.concurrency.AtomicInt
import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import com.toxicbakery.game.dungeon.auth.Credentials
import com.toxicbakery.game.dungeon.client.ExpectedResponseType
import com.toxicbakery.game.dungeon.exception.AuthenticationException
import com.toxicbakery.game.dungeon.exception.NoPlayerWithUsernameException
import com.toxicbakery.game.dungeon.machine.Machine
import com.toxicbakery.game.dungeon.machine.command.CommandMachine
import com.toxicbakery.game.dungeon.manager.AuthenticationManager
import com.toxicbakery.game.dungeon.model.session.GameSession
import kotlinx.coroutines.delay
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.factory
import org.kodein.di.erased.instance

private class AuthenticationMachineImpl(
    private val authenticationManager: AuthenticationManager,
    private val commandMachineFactory: (GameSession) -> CommandMachine,
    private val gameSession: GameSession
) : AuthenticationMachine {

    private val authenticationErrorCount = AtomicInt(0)
    private val _currentState: AtomicReference<AuthenticationState> = AtomicReference(AuthenticationState.Init)
    private var credentials: Credentials = Credentials()

    override val name: String = "AuthenticationMachine"

    override val currentState: AuthenticationState
        get() = _currentState.value

    override suspend fun acceptMessage(message: String): Machine<*> {
        _currentState.value = cycle(message)
        return when (currentState) {
            AuthenticationState.Authenticated -> commandMachineFactory(gameSession)
            else -> this
        }
    }

    override suspend fun initMachine() {
        _currentState.value = initAuthentication()
    }

    private suspend fun cycle(message: String): AuthenticationState = when (_currentState.value) {
        AuthenticationState.Init -> initAuthentication()
        AuthenticationState.AwaitingUsername -> takeUsernameAndProceed(message)
        AuthenticationState.AwaitingPassword -> takePasswordAndProceed(message)
        AuthenticationState.Authenticated -> error("Request to process on end state")
    }

    private suspend fun initAuthentication(): AuthenticationState =
        if (authenticationErrorCount.incrementAndGet() > MAX_AUTH_ATTEMPTS) tooManyAttempts()
        else {
            credentials = Credentials()
            gameSession.send("What is your username?")
            AuthenticationState.AwaitingUsername
        }

    private suspend fun takeUsernameAndProceed(username: String): AuthenticationState {
        credentials = credentials.copy(username = username)
        gameSession.send("What is your password?", ExpectedResponseType.Secure)
        return AuthenticationState.AwaitingPassword
    }

    private suspend fun takePasswordAndProceed(password: String): AuthenticationState {
        credentials = credentials.copy(password = password)
        return try {
            authenticationManager.authenticatePlayer(credentials, gameSession)
            gameSession.send("Authentication successful! Welcome back ${credentials.username}")
            AuthenticationState.Authenticated
        } catch (e: NoPlayerWithUsernameException) {
            gameSession.send("User not found, are you sure you have registered?")
            initAuthentication()
        } catch (e: AuthenticationException) {
            gameSession.send("Authentication failed, check your password and try again.")
            initAuthentication()
        }
    }

    private suspend fun tooManyAttempts(): AuthenticationState {
        delay(AUTH_FAILURE_TIMEOUT)
        gameSession.send("Too many failed authentication attempts.")
        gameSession.close()
        return AuthenticationState.Init
    }

    companion object {
        private const val AUTH_FAILURE_TIMEOUT = 5000L
        private const val MAX_AUTH_ATTEMPTS = 3
    }

}

interface AuthenticationMachine : Machine<AuthenticationState>

val authenticationMachineModule = Kodein.Module("authenticationMachineModule") {
    bind<AuthenticationMachine>() with factory { gameSession: GameSession ->
        AuthenticationMachineImpl(
            authenticationManager = instance(),
            commandMachineFactory = factory(),
            gameSession = gameSession
        )
    }
}
