package com.toxicbakery.game.dungeon.persistence

import com.toxicbakery.game.dungeon.map.DistanceFilter
import com.toxicbakery.game.dungeon.model.Lookable.Player
import com.toxicbakery.game.dungeon.model.auth.Credentials
import com.toxicbakery.game.dungeon.model.world.Location
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.singleton

private class DatabaseImpl(
    private val persistenceDelegate: PersistenceDelegate
) : Database {

    override suspend fun authenticatePlayer(
        credentials: Credentials
    ): Player = persistenceDelegate.authenticatePlayer(credentials)

    override suspend fun updatePlayer(player: Player) = persistenceDelegate.updatePlayer(player)

    override suspend fun createPlayer(
        credentials: Credentials
    ): Player = persistenceDelegate.createPlayer(credentials)

    override suspend fun getPlayerById(id: Int) = persistenceDelegate.getPlayerById(id)

    override suspend fun getPlayersNear(
        location: Location,
        distanceFilter: DistanceFilter
    ): List<Player> = persistenceDelegate.getPlayersNear(location, distanceFilter)
}

interface Database {

    suspend fun authenticatePlayer(credentials: Credentials): Player

    suspend fun updatePlayer(player: Player)

    suspend fun createPlayer(credentials: Credentials): Player

    suspend fun getPlayerById(id: Int): Player

    suspend fun getPlayersNear(location: Location, distanceFilter: DistanceFilter): List<Player>
}

val databaseModule = Kodein.Module("databaseModule") {
    bind<Database>() with singleton {
        DatabaseImpl(
            persistenceDelegate = InMemoryPersistenceDelegate
        )
    }
}
