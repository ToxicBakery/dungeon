package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.map.DistanceFilter
import com.toxicbakery.game.dungeon.model.character.Location
import com.toxicbakery.game.dungeon.model.character.Player
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.persistence.Database
import com.toxicbakery.game.dungeon.persistence.store.DungeonStateStore
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
        .let { dungeonState ->
            val playerId = dungeonState.getGameSession(gameSession).playerId
            dungeonState.getPlayerSessionById(playerId).player
        }

    override suspend fun updatePlayer(
        player: Player,
        gameSession: GameSession
    ) = coroutineScope {
        val t1 = async { database.updatePlayer(player) }
        val t2 = async { dungeonStateStore.modify { dungeonState -> dungeonState.updatePlayer(player) } }
        t1.await()
        t2.await()
    }

    override suspend fun getPlayersNear(
        location: Location,
        distanceFilter: DistanceFilter
    ): List<Player> = database.getPlayersNear(location, distanceFilter)

}

interface PlayerManager {

    suspend fun getPlayerByGameSession(
        gameSession: GameSession
    ): Player

    suspend fun updatePlayer(
        player: Player,
        gameSession: GameSession
    )

    suspend fun getPlayersNear(
        location: Location,
        distanceFilter: DistanceFilter
    ): List<Player>

}

val playerManagerModule = Kodein.Module("playerManagerModule") {
    bind<PlayerManager>() with provider {
        PlayerManagerImpl(
            dungeonStateStore = instance(),
            database = instance()
        )
    }
}
