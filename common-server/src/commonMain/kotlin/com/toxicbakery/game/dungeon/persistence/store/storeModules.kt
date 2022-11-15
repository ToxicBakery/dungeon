package com.toxicbakery.game.dungeon.persistence.store

import org.kodein.di.DI

val storeModules = DI.Module("storeModules") {
    import(dungeonStateStoreModule)
    import(gameClockModule)
}
