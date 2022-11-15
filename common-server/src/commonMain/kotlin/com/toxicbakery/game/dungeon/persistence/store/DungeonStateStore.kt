package com.toxicbakery.game.dungeon.persistence.store

import com.toxicbakery.game.dungeon.model.DungeonState
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

private class DungeonStateStoreImpl(
    initialValue: DungeonState
) : BroadcastChannelStore<DungeonState>(initialValue), DungeonStateStore

interface DungeonStateStore : ChannelStore<DungeonState>

val dungeonStateStoreModule = DI.Module("dungeonStateStoreModule") {
    bind<DungeonStateStore>() with singleton {
        DungeonStateStoreImpl(
            initialValue = DungeonState()
        )
    }
}
