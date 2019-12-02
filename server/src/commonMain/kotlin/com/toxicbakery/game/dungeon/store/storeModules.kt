package com.toxicbakery.game.dungeon.store

import org.kodein.di.Kodein

val storeModules = Kodein.Module("storeModules") {
    import(dungeonStateStoreModule)
    import(gameClockModule)
}
