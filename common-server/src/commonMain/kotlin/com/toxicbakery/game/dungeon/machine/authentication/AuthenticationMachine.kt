package com.toxicbakery.game.dungeon.machine.authentication

import com.toxicbakery.game.dungeon.exception.AuthenticationException
import com.toxicbakery.game.dungeon.exception.NoPlayerWithUsernameException
import com.toxicbakery.game.dungeon.machine.Machine
import com.toxicbakery.game.dungeon.machine.command.CommandMachine
import com.toxicbakery.game.dungeon.manager.AuthenticationManager
import com.toxicbakery.game.dungeon.model.auth.Credentials
import com.toxicbakery.game.dungeon.model.client.ExpectedResponseType
import com.toxicbakery.game.dungeon.model.session.GameSession
import kotlinx.coroutines.delay
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private class AuthenticationMachineImpl(
    private val authenticationManager: AuthenticationManager,
    private val commandMachine: CommandMachine,
    private val authMachineState: AuthMachineState = AuthMachineState()
) : AuthenticationMachine {

    override val currentState: AuthenticationState = authMachineState.state

    override val name: String = "AuthenticationMachine"

    override suspend fun acceptMessage(
        gameSession: GameSession,
        message: String
    ): Machine<*> = gameSession.cycle(message).let { nextState ->
        when (nextState.state) {
            AuthenticationState.Authenticated -> commandMachine
            else -> AuthenticationMachineImpl(
                authenticationManager = authenticationManager,
                commandMachine = commandMachine,
                authMachineState = nextState
            )
        }
    }

    override suspend fun initMachine(gameSession: GameSession) = when (currentState) {
        AuthenticationState.Init -> AuthenticationMachineImpl(
            authenticationManager = authenticationManager,
            commandMachine = commandMachine,
            authMachineState = gameSession.cycle()
        )
        else -> this
    }

    private suspend fun GameSession.cycle(message: String = ""): AuthMachineState = when (currentState) {
        AuthenticationState.Init -> initAuthentication()
        AuthenticationState.AwaitingUsername -> takeUsernameAndProceed(message)
        AuthenticationState.AwaitingPassword -> takePasswordAndProceed(message)
        AuthenticationState.Authenticated -> error("Request to process on end state")
    }

    private suspend fun GameSession.initAuthentication(): AuthMachineState =
        if (authMachineState.errorCount > MAX_AUTH_ATTEMPTS) tooManyAttempts()
        else {
            sendMessage("What is your username?")
            authMachineState.copy(
                state = AuthenticationState.AwaitingUsername,
                credentials = Credentials()
            )
        }

    private suspend fun GameSession.takeUsernameAndProceed(username: String): AuthMachineState {
        sendMessage("What is your password?", ExpectedResponseType.Secure)
        return authMachineState.copy(
            state = AuthenticationState.AwaitingPassword,
            credentials = Credentials(username = username)
        )
    }

    private suspend fun GameSession.takePasswordAndProceed(password: String): AuthMachineState = try {
        val credentials = authMachineState.credentials.copy(password = password)
        authenticationManager.authenticatePlayer(credentials, this)
        sendMessage("Authentication successful! Welcome back ${credentials.username}")
        authMachineState.copy(
            state = AuthenticationState.Authenticated,
            credentials = credentials
        )
    } catch (e: NoPlayerWithUsernameException) {
        sendMessage("User not found, are you sure you have registered?")
        initAuthentication()
    } catch (e: AuthenticationException) {
        sendMessage("Authentication failed, check your password and try again.")
        initAuthentication()
    }

    private suspend fun GameSession.tooManyAttempts(): AuthMachineState {
        delay(AUTH_FAILURE_TIMEOUT)
        sendMessage("Too many failed authentication attempts.")
        close()
        return AuthMachineState(state = AuthenticationState.Init)
    }

    companion object {
        private const val AUTH_FAILURE_TIMEOUT = 5000L
        private const val MAX_AUTH_ATTEMPTS = 3
    }

}

private data class AuthMachineState(
    val errorCount: Int = 0,
    val credentials: Credentials = Credentials(),
    val state: AuthenticationState = AuthenticationState.Init
)

interface AuthenticationMachine : Machine<AuthenticationState>

val authenticationMachineModule = Kodein.Module("authenticationMachineModule") {
    bind<AuthenticationMachine>() with provider {
        AuthenticationMachineImpl(
            authenticationManager = instance(),
            commandMachine = instance()
        )
    }
}
