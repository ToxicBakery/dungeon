package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.manager.GameSessionManager
import com.toxicbakery.game.dungeon.manager.NpcManager
import com.toxicbakery.game.dungeon.model.Lookable.*
import com.toxicbakery.game.dungeon.model.character.stats.Stats
import com.toxicbakery.game.dungeon.model.client.ClientMessage
import com.toxicbakery.game.dungeon.model.client.ClientMessage.UserMessage
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.model.world.Location
import com.toxicbakery.logging.Arbor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.protobuf.ProtoBuf
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

@OptIn(ExperimentalSerializationApi::class)
private class DungeonServerImpl(
    private val gameSessionManager: GameSessionManager,
    private val npcManager: NpcManager,
) : DungeonServer {

    init {
        CoroutineScope(gameProcessingDispatcher).launch {
            npcManager.createNpc(
                Animal(
                    name = "Sheep",
                    stats = Stats(health = 100),
                    statsBase = Stats(health = 100),
                    location = Location(),
                    isPassive = true,
                )
            )
        }
    }

    override suspend fun receivedMessage(
        session: GameSession,
        message: String
    ) {
        Arbor.d("Received message from session %s", session.sessionId)
        when (val clientMessage = ProtoBuf.decodeFromHexString(ClientMessage.serializer(), message)) {
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

val dungeonServerModule = DI.Module("dungeonServerModule") {
    bind<DungeonServer>() with singleton {
        DungeonServerImpl(
            gameSessionManager = instance(),
            npcManager = instance(),
        )
    }
}
