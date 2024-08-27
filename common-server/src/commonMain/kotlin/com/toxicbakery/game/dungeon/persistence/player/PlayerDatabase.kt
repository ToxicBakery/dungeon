package com.toxicbakery.game.dungeon.persistence.player

import com.toxicbakery.game.dungeon.model.Lookable.Player
import com.toxicbakery.game.dungeon.model.auth.Credentials
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

private class PlayerDatabaseImpl(
    private val persistenceDelegate: PersistencePlayerDatabaseDelegate
) : PlayerDatabase {

    override suspend fun players(): List<Player> = persistenceDelegate.players()

    override suspend fun authenticatePlayer(
        credentials: Credentials
    ): Player = persistenceDelegate.authenticatePlayer(credentials)

    override suspend fun updatePlayer(player: Player) = persistenceDelegate.updatePlayer(player)

    override suspend fun createPlayer(
        credentials: Credentials
    ): Player = persistenceDelegate.createPlayer(credentials)

    override suspend fun getPlayerById(id: String) = persistenceDelegate.getPlayerById(id)
}

interface PlayerDatabase {

    suspend fun players(): List<Player>

    suspend fun authenticatePlayer(credentials: Credentials): Player

    suspend fun updatePlayer(player: Player)

    suspend fun createPlayer(credentials: Credentials): Player

    suspend fun getPlayerById(id: String): Player
}

val playerDatabaseModule = DI.Module("playerDatabaseModule") {
    bind<PlayerDatabase>() with singleton {
        PlayerDatabaseImpl(
            persistenceDelegate = InMemoryPersistencePlayerDatabaseDelegate
        )
    }
}
