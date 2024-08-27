package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.defaults.AnimalGenerator
import com.toxicbakery.game.dungeon.defaults.BaseAnimal
import com.toxicbakery.game.dungeon.manager.GameSessionManager
import com.toxicbakery.game.dungeon.manager.NpcManager
import com.toxicbakery.game.dungeon.model.client.ClientMessage
import com.toxicbakery.game.dungeon.model.client.ClientMessage.UserMessage
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.persistence.store.GameClock
import com.toxicbakery.logging.Arbor
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.protobuf.ProtoBuf
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

@OptIn(ExperimentalSerializationApi::class, FlowPreview::class)
private class DungeonServerImpl(
    private val animalGenerator: AnimalGenerator,
    private val gameSessionManager: GameSessionManager,
    private val gameClock: GameClock,
    private val npcManager: NpcManager,
) : DungeonServer {

    private val animalGenerationFlow: Flow<Long>
        get() = gameClock.gameTickFlow
            .filter { npcManager.getNpcCount() < MAX_NPC_COUNT }
            .onEach {
                val animal = animalGenerator.create(BaseAnimal.pickNextAnimal())
                npcManager.createNpc(animal)
                println("Animal spawned ${animal.name} at ${animal.location}")
            }
            .catch { e ->
                println("Failed to spawn animal: ${e.message}")
                emitAll(animalGenerationFlow)
            }

    init {
        tickScope.launch {
            animalGenerationFlow
                .launchIn(gameProcessingScope)
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

    companion object {
        private const val MAX_NPC_COUNT = 100
    }
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
            animalGenerator = instance(),
            gameClock = instance(),
            gameSessionManager = instance(),
            npcManager = instance(),
        )
    }
}
