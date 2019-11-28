package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.machine.GameMachine
import com.toxicbakery.game.dungeon.model.DungeonState
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.store.BroadcastChannelStore
import com.toxicbakery.game.dungeon.store.ChannelStore
import com.toxicbakery.game.dungeon.store.DungeonStateStore
import com.toxicbakery.logging.Arbor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.factory
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private class GameSessionManagerImpl(
    private val dungeonStateStore: DungeonStateStore,
    private val gameMachineFactory: (GameSession) -> GameMachine
) : GameSessionManager {

    private val gameMachineStore: ChannelStore<Map<GameSession, GameMachine>> =
        object : BroadcastChannelStore<Map<GameSession, GameMachine>>(mapOf()) {}

    override suspend fun observeGameSessions(): Flow<List<GameSession>> = dungeonStateStore
        .observe()
        .map { dungeonState: DungeonState -> dungeonState.gameSessionList }

    override suspend fun receivedMessage(session: GameSession, message: String) {
        gameMachineStore.observe()
            .first()[session]
            ?.receivedMessage(message)
            ?: Arbor.d("Failed to notify msg for %s", session.sessionId)
    }

    override suspend fun sessionCreated(
        session: GameSession
    ) {
        dungeonStateStore.modify { dungeonState ->
            dungeonState[session]?.close()
            dungeonState + session
        }
        gameMachineStore.modify { gameMachineMap ->
            val gameMachine = gameMachineFactory(session)
            gameMachine.initMachine()
            gameMachineMap + (session to gameMachine)
        }
    }

    override suspend fun sessionDestroyed(
        session: GameSession
    ) {
        dungeonStateStore.modify { dungeonState ->
            dungeonState - session
        }
        gameMachineStore.modify { gameMachineMap ->
            gameMachineMap - session
        }
    }

}

interface GameSessionManager {

    /**
     * Monitor changes to the list of known game sessions.
     */
    suspend fun observeGameSessions(): Flow<List<GameSession>>

    /**
     * A message was received for a given session.
     */
    suspend fun receivedMessage(
        session: GameSession,
        message: String
    )

    /**
     * A player has connected to the server but has not authenticated yet.
     */
    suspend fun sessionCreated(
        session: GameSession
    )

    /**
     * A player forcefully left the server.
     */
    suspend fun sessionDestroyed(
        session: GameSession
    )

}

val gameSessionManagerModule = Kodein.Module("gameSessionManagerModule") {
    bind<GameSessionManager>() with provider {
        GameSessionManagerImpl(
            dungeonStateStore = instance(),
            gameMachineFactory = factory()
        )
    }
}
