package com.toxicbakery.game.dungeon.store

import com.toxicbakery.game.dungeon.model.DungeonState
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.singleton

private class DungeonStateStoreImpl(
    initialValue: DungeonState
) : BroadcastChannelStore<DungeonState>(initialValue), DungeonStateStore

interface DungeonStateStore : ChannelStore<DungeonState>

val dungeonStateStoreModule = Kodein.Module("dungeonStateStoreModule") {
    bind<DungeonStateStore>() with singleton {
        DungeonStateStoreImpl(
            initialValue = DungeonState()
        )
    }
}
