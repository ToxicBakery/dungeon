package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.map.mapManagerModule
import org.kodein.di.Kodein

val serverManagerModules = Kodein.Module("serverManagerModules") {
    import(authenticationManagerModule)
    import(communicationManagerModule)
    import(gameSessionManagerModule)
    import(mapManagerModule)
    import(playerDataManagerModule)
    import(playerManagerModule)
    import(worldManagerModule)
}
