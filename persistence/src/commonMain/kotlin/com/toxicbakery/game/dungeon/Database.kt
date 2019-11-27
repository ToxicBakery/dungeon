package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.auth.Credentials
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.singleton

private class DatabaseImpl(
    private val persistenceDelegate: PersistenceDelegate
) : Database {

    override fun authenticatePlayer(
        id: String,
        credentials: Credentials
    ): Player = persistenceDelegate.authenticatePlayer(id, credentials)

    override fun createPlayer(
        player: Player,
        credentials: Credentials
    ): Player = persistenceDelegate.createPlayer(player, credentials)

    override fun getPlayerById(id: String) = persistenceDelegate.getPlayerById(id)

}

interface Database {

    fun authenticatePlayer(
        id: String,
        credentials: Credentials
    ): Player

    fun createPlayer(
        player: Player,
        credentials: Credentials
    ): Player

    fun getPlayerById(id: String): Player

}

val databaseModule = Kodein.Module("databaseModule") {
    bind<Database>() with singleton {
        DatabaseImpl(
            persistenceDelegate = InMemoryPersistenceDelegate
        )
    }
}
