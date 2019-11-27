package com.toxicbakery.game.dungeon.manager

import org.kodein.di.Kodein

val serverManagerModules = Kodein.Module("serverManagerModules") {
    import(authenticationManagerModule)
    import(playerManagerModule)
    import(gameSessionManagerModule)
}
