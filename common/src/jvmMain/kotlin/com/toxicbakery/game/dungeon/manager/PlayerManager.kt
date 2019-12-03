package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.model.character.Player
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.persistence.Database
import com.toxicbakery.game.dungeon.persistence.store.DungeonStateStore
import kotlinx.coroutines.flow.first
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private class PlayerManagerImpl(
    private val dungeonStateStore: DungeonStateStore,
    private val database: Database
) : PlayerManager {

    override suspend fun getPlayerByGameSession(
        gameSession: GameSession
    ): Player = dungeonStateStore
        .observe()
        .first()
        .let { dungeonState -> database.getPlayerById(dungeonState[gameSession]!!.playerId) }

    override suspend fun updatePlayer(
        player: Player,
        gameSession: GameSession
    ) {
        database.updatePlayer(player)
        dungeonStateStore.modify {dungeonState ->
            dungeonState.set(player, gameSession)
        }
    }

}

interface PlayerManager {

    suspend fun getPlayerByGameSession(
        gameSession: GameSession
    ): Player

    suspend fun updatePlayer(
        player: Player,
        gameSession: GameSession
    )

}

val playerManagerModule = Kodein.Module("playerManagerModule") {
    bind<PlayerManager>() with provider {
        PlayerManagerImpl(
            dungeonStateStore = instance(),
            database = instance()
        )
    }
}
