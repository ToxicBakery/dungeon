package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.Database
import com.toxicbakery.game.dungeon.Player
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.model.session.PlayerSession
import com.toxicbakery.game.dungeon.store.DungeonStateStore
import kotlinx.coroutines.flow.first
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

class PlayerManager(
    private val dungeonStateStore: DungeonStateStore,

    private val database: Database
) {

    suspend fun playerLeft(
        player: Player
    ) = dungeonStateStore.modify { dungeonState ->
        dungeonState - player
    }

    /**
     * Registered players that have authenticated.
     */
    suspend fun authenticatedPlayers(): List<PlayerSession> = dungeonStateStore
        .observe()
        .first()
        .playerSessionsList

}

val playerManagerModule = Kodein.Module("playerManagerModule") {
    bind<PlayerManager>() with provider {
        PlayerManager(
            dungeonStateStore = instance(),
            database = instance()
        )
    }
}
