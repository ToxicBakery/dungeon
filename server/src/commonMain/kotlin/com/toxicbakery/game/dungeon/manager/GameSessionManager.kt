package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.model.DungeonState
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.store.DungeonStateStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private class GameSessionManagerImpl(
    private val dungeonStateStore: DungeonStateStore
) : GameSessionManager {

    override suspend fun observeGameSessions(): Flow<List<GameSession>> = dungeonStateStore
        .observe()
        .map { dungeonState: DungeonState -> dungeonState.gameSessionList }

    override suspend fun sessionCreated(
        session: GameSession
    ) = dungeonStateStore.modify { dungeonState ->
        dungeonState[session]?.close()
        dungeonState + session
    }

    override suspend fun sessionDestroyed(
        session: GameSession
    ) = dungeonStateStore.modify { dungeonState ->
        dungeonState - session
    }

}

interface GameSessionManager {

    /**
     * Monitor changes to the list of known game sessions.
     */
    suspend fun observeGameSessions(): Flow<List<GameSession>>

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
            dungeonStateStore = instance()
        )
    }
}
