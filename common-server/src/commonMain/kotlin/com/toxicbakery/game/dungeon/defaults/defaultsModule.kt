package com.toxicbakery.game.dungeon.defaults

import org.kodein.di.DI

val defaultsModule = DI.Module("defaultModule") {
    import(animalGeneratorModule)
}
