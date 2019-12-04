package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.machine.machineModules
import com.toxicbakery.game.dungeon.manager.serverManagerModules
import com.toxicbakery.game.dungeon.persistence.databaseModule
import com.toxicbakery.game.dungeon.persistence.store.storeModules
import org.kodein.di.Kodein

val commonApplicationKodein = Kodein {
    import(databaseModule)
    import(machineModules)
    import(serverManagerModules)
    import(storeModules)
}
