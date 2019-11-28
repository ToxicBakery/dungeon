package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.Database
import com.toxicbakery.game.dungeon.character.Player
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.store.DungeonStateStore
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private class PlayerManagerImpl(
    private val dungeonStateStore: DungeonStateStore,
    private val database: Database
) : PlayerManager {

    override suspend fun changeName(
        player: Player,
        gameSession: GameSession
    ) {
        database.changeName(player)
        dungeonStateStore.modify {dungeonState ->
            dungeonState.set(player, gameSession)
        }
    }

}

interface PlayerManager {

    suspend fun changeName(
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
