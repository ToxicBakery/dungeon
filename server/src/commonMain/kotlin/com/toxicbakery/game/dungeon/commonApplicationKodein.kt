package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.machine.machineModules
import com.toxicbakery.game.dungeon.manager.serverManagerModules
import com.toxicbakery.game.dungeon.store.dungeonStateStoreModule
import org.kodein.di.Kodein

val commonApplicationKodein = Kodein {
    import(databaseModule)
    import(dungeonStateStoreModule)
    import(machineModules)
    import(serverManagerModules)
}
