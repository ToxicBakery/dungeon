package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.manager.GameSessionManager
import com.toxicbakery.game.dungeon.manager.PlayerManager
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.logging.Arbor
import kotlinx.coroutines.flow.first
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton

private class DungeonServerImpl(
    private val playerManager: PlayerManager,
    private val gameSessionManager: GameSessionManager
) : DungeonServer {

    override suspend fun receivedMessage(
        session: GameSession,
        message: String
    ) {
        Arbor.d("Received text from session %s", session.sessionId)
        broadcastMessage(message)
    }

    override suspend fun onNewSession(session: GameSession) = gameSessionManager.sessionCreated(session)

    override suspend fun onLostSession(session: GameSession) = gameSessionManager.sessionDestroyed(session)

    private suspend fun broadcastMessage(message: String) = gameSessionManager
        .observeGameSessions()
        .first()
        .forEach { gameSession -> gameSession.send(message) }

}

interface DungeonServer {

    suspend fun receivedMessage(
        session: GameSession,
        message: String
    )

    suspend fun onNewSession(
        session: GameSession
    )

    suspend fun onLostSession(
        session: GameSession
    )

}

val dungeonServerModule = Kodein.Module("dungeonServerModule") {
    bind<DungeonServer>() with singleton {
        DungeonServerImpl(
            playerManager = instance(),
            gameSessionManager = instance()
        )
    }
}
