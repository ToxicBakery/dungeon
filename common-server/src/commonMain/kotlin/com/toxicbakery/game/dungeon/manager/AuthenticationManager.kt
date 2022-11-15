package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.model.Lookable.Player
import com.toxicbakery.game.dungeon.model.auth.Credentials
import com.toxicbakery.game.dungeon.model.session.AuthenticatedGameSession
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.model.session.PlayerSession
import com.toxicbakery.game.dungeon.persistence.player.PlayerDatabase
import com.toxicbakery.game.dungeon.persistence.store.DungeonStateStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

private class AuthenticationManagerImpl(
    private val playerDatabase: PlayerDatabase,
    private val dungeonStateStore: DungeonStateStore,
    private val communicationManager: CommunicationManager,
) : AuthenticationManager {

    override suspend fun authenticatedPlayers(): Flow<List<PlayerSession>> = dungeonStateStore
        .observe()
        .map { dungeonState -> dungeonState.playerSessionsList }

    override suspend fun authenticatePlayer(
        credentials: Credentials,
        gameSession: GameSession
    ): Player {
        val player = playerDatabase.authenticatePlayer(credentials)
        val authenticatedGameSession = AuthenticatedGameSession(player.id, gameSession)
        dungeonStateStore.modify { dungeonState ->
            dungeonState.setAuthenticatedPlayer(player, authenticatedGameSession)
        }
        communicationManager.serverMessage(
            message = "${credentials.username} has joined.",
            excludedPlayer = player,
        )
        return player
    }

    override suspend fun registerPlayer(
        credentials: Credentials
    ) = playerDatabase.createPlayer(credentials)

    override suspend fun playerLeft(player: Player) {
        dungeonStateStore.modify { dungeonState -> dungeonState.removePlayerAndSession(player) }
        communicationManager.serverMessage("${player.name} has left.")
    }
}

interface AuthenticationManager {

    /**
     * Registered players that have authenticated.
     */
    suspend fun authenticatedPlayers(): Flow<List<PlayerSession>>

    suspend fun authenticatePlayer(
        credentials: Credentials,
        gameSession: GameSession
    ): Player

    suspend fun playerLeft(player: Player)

    suspend fun registerPlayer(
        credentials: Credentials
    ): Player
}

val authenticationManagerModule = DI.Module("authenticationManagerModule") {
    bind<AuthenticationManager>() with provider {
        AuthenticationManagerImpl(
            playerDatabase = instance(),
            dungeonStateStore = instance(),
            communicationManager = instance(),
        )
    }
}
