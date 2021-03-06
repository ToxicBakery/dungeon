package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.machine.Machine
import com.toxicbakery.game.dungeon.machine.init.InitMachine
import com.toxicbakery.game.dungeon.model.DungeonState
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.persistence.store.BroadcastChannelStore
import com.toxicbakery.game.dungeon.persistence.store.ChannelStore
import com.toxicbakery.game.dungeon.persistence.store.DungeonStateStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private class GameSessionManagerImpl(
    private val dungeonStateStore: DungeonStateStore,
    private val initMachine: Machine<*>
) : GameSessionManager {

    private val gameMachineStore: ChannelStore<Map<String, Machine<*>>> =
        object : BroadcastChannelStore<Map<String, Machine<*>>>(mapOf()) {}

    override suspend fun observeGameSessions(): Flow<List<GameSession>> = dungeonStateStore
        .observe()
        .map { dungeonState: DungeonState -> dungeonState.gameSessionList }

    override suspend fun receivedMessage(
        gameSession: GameSession,
        message: String
    ) {
        gameMachineStore.modify { gameMachineMap ->
            val currentMachine = requireNotNull(gameMachineMap[gameSession.sessionId])
            val nextMachine = currentMachine.acceptMessage(gameSession, message)
                .initMachine(gameSession)
            gameMachineMap + (gameSession.sessionId to nextMachine)
        }
    }

    override suspend fun sessionCreated(gameSession: GameSession) {
        dungeonStateStore.modify { dungeonState ->
            dungeonState.getAuthenticatedGameSession(gameSession)?.close()
            dungeonState.addUnauthenticatedSession(gameSession)
        }
        gameMachineStore.modify { gameMachineMap ->
            val nextMachine = initMachine.initMachine(gameSession)
            gameMachineMap + (gameSession.sessionId to nextMachine)
        }
    }

    override suspend fun sessionDestroyed(gameSession: GameSession) {
        dungeonStateStore.modify { dungeonState -> dungeonState.removePlayerAndSession(gameSession) }
        gameMachineStore.modify { gameMachineMap -> gameMachineMap - gameSession.sessionId }
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
        gameSession: GameSession,
        message: String
    )

    /**
     * A player has connected to the server but has not authenticated yet.
     */
    suspend fun sessionCreated(
        gameSession: GameSession
    )

    /**
     * A player forcefully left the server.
     */
    suspend fun sessionDestroyed(
        gameSession: GameSession
    )

}

val gameSessionManagerModule = Kodein.Module("gameSessionManagerModule") {
    bind<GameSessionManager>() with provider {
        GameSessionManagerImpl(
            dungeonStateStore = instance(),
            initMachine = instance<InitMachine>()
        )
    }
}
