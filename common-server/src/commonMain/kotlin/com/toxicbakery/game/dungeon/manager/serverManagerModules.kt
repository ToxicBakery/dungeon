package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.map.mapManagerModule
import org.kodein.di.DI

val serverManagerModules = DI.Module("serverManagerModules") {
    import(authenticationManagerModule)
    import(communicationManagerModule)
    import(drawManagerModule)
    import(gameSessionManagerModule)
    import(lookManagerModule)
    import(mapManagerModule)
    import(npcManagerModule)
    import(playerDataManagerModule)
    import(playerManagerModule)
    import(worldManagerModule)
}
