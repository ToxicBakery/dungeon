package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.client.ClientMessage
import com.toxicbakery.game.dungeon.client.ClientMessage.UserMessage
import com.toxicbakery.game.dungeon.manager.GameSessionManager
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.logging.Arbor
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton

private class DungeonServerImpl(
    private val gameSessionManager: GameSessionManager
) : DungeonServer {

    override suspend fun receivedMessage(
        session: GameSession,
        message: String
    ) {
        Arbor.d("Received text from session %s", session.sessionId)
        val userMessage: UserMessage = Json(jsonConfiguration).parse(message)
        gameSessionManager.receivedMessage(session, userMessage.message)
    }

    override suspend fun onNewSession(session: GameSession) = gameSessionManager.sessionCreated(session)

    override suspend fun onLostSession(session: GameSession) = gameSessionManager.sessionDestroyed(session)

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
            gameSessionManager = instance()
        )
    }
}
