package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.model.client.ClientMessage
import com.toxicbakery.game.dungeon.model.client.ClientMessage.UserMessage
import com.toxicbakery.game.dungeon.manager.GameSessionManager
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.logging.Arbor
import kotlinx.serialization.loads
import kotlinx.serialization.protobuf.ProtoBuf
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton

private class DungeonServerImpl(
    private val gameSessionManager: GameSessionManager
) : DungeonServer {

    private val protoBuf: ProtoBuf = ProtoBuf()

    override suspend fun receivedMessage(
        session: GameSession,
        message: String
    ) {
        Arbor.d("Received message from session %s (%s)", session.sessionId, message)
        when (val clientMessage = protoBuf.loads(ClientMessage.serializer(), message)) {
            is UserMessage -> gameSessionManager.receivedMessage(session, clientMessage.message)
            else -> error("Unexpected ClientMessage ${clientMessage::class.simpleName}")
        }
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