package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.defaults.defaultsModule
import com.toxicbakery.game.dungeon.machine.machineModules
import com.toxicbakery.game.dungeon.manager.nearbyManagerModule
import com.toxicbakery.game.dungeon.manager.serverManagerModules
import com.toxicbakery.game.dungeon.persistence.npc.npcDatabaseModule
import com.toxicbakery.game.dungeon.persistence.player.playerDatabaseModule
import com.toxicbakery.game.dungeon.persistence.store.storeModules
import com.toxicbakery.game.dungeon.util.diceRollModule
import org.kodein.di.DI

val commonApplicationKodein = DI {
    import(defaultsModule)
    import(diceRollModule)
    import(nearbyManagerModule)
    import(npcDatabaseModule)
    import(playerDatabaseModule)
    import(machineModules)
    import(serverManagerModules)
    import(storeModules)
}
