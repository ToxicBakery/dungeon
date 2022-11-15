package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.machine.machineModules
import com.toxicbakery.game.dungeon.manager.serverManagerModules
import com.toxicbakery.game.dungeon.persistence.npc.npcDatabaseModule
import com.toxicbakery.game.dungeon.persistence.player.playerDatabaseModule
import com.toxicbakery.game.dungeon.persistence.store.storeModules
import org.kodein.di.Kodein

val commonApplicationKodein = Kodein {
    import(npcDatabaseModule)
    import(playerDatabaseModule)
    import(machineModules)
    import(serverManagerModules)
    import(storeModules)
}
