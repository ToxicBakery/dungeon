package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.map.DistanceFilter
import com.toxicbakery.game.dungeon.model.Lookable.Player
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.model.session.PlayerSession
import com.toxicbakery.game.dungeon.model.world.Location
import com.toxicbakery.game.dungeon.persistence.player.PlayerDatabase
import com.toxicbakery.game.dungeon.persistence.store.DungeonStateStore
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

private class PlayerManagerImpl(
    private val dungeonStateStore: DungeonStateStore,
    private val playerDatabase: PlayerDatabase,
) : PlayerManager {

    override suspend fun getPlayerByGameSession(
        gameSession: GameSession
    ): Player = dungeonStateStore
        // TODO Can this just be `.value()` ?
        .observe()
        .first()
        .let { dungeonState ->
            val playerId = dungeonState.getGameSession(gameSession).playerId
            dungeonState.getPlayerSessionById(playerId).player
        }

    override suspend fun updatePlayer(
        player: Player
    ) = coroutineScope {
        playerDatabase.updatePlayer(player)
        dungeonStateStore.modify { dungeonState -> dungeonState.updatePlayer(player) }
    }

    override suspend fun getPlayersNear(
        location: Location,
        distanceFilter: DistanceFilter
    ): List<Player> = playerDatabase.players()
        .filter { player -> distanceFilter.nearby(location, player.location) }

    override suspend fun getPlayersAt(location: Location): List<Player> =
        dungeonStateStore.value().getPlayersAt(location).map(PlayerSession::player)
}

interface PlayerManager {

    suspend fun getPlayerByGameSession(
        gameSession: GameSession
    ): Player

    suspend fun updatePlayer(
        player: Player
    )

    suspend fun getPlayersNear(
        location: Location,
        distanceFilter: DistanceFilter
    ): List<Player>

    suspend fun getPlayersAt(
        location: Location
    ): List<Player>
}

val playerManagerModule = DI.Module("playerManagerModule") {
    bind<PlayerManager>() with singleton {
        PlayerManagerImpl(
            dungeonStateStore = instance(),
            playerDatabase = instance(),
        )
    }
}
