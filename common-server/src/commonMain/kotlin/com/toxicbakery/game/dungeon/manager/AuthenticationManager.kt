package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.model.auth.Credentials
import com.toxicbakery.game.dungeon.model.character.Player
import com.toxicbakery.game.dungeon.model.session.AuthenticatedGameSession
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.model.session.PlayerSession
import com.toxicbakery.game.dungeon.persistence.Database
import com.toxicbakery.game.dungeon.persistence.store.DungeonStateStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private class AuthenticationManagerImpl(
    private val database: Database,
    private val dungeonStateStore: DungeonStateStore
) : AuthenticationManager {

    override suspend fun authenticatedPlayers(): Flow<List<PlayerSession>> = dungeonStateStore
        .observe()
        .map { dungeonState -> dungeonState.playerSessionsList }

    override suspend fun authenticatePlayer(
        credentials: Credentials,
        gameSession: GameSession
    ): Player {
        val player = database.authenticatePlayer(credentials)
        val authenticatedGameSession = AuthenticatedGameSession(player.id, gameSession)
        dungeonStateStore.modify { dungeonState ->
            dungeonState.setAuthenticatedPlayer(player, authenticatedGameSession)
        }
        return player
    }

    override suspend fun registerPlayer(
        credentials: Credentials
    ) = database.createPlayer(credentials)

    override suspend fun playerLeft(
        player: Player
    ) = dungeonStateStore.modify { dungeonState -> dungeonState.removePlayerAndSession(player) }

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

val authenticationManagerModule = Kodein.Module("authenticationManagerModule") {
    bind<AuthenticationManager>() with provider {
        AuthenticationManagerImpl(
            database = instance(),
            dungeonStateStore = instance()
        )
    }
}
