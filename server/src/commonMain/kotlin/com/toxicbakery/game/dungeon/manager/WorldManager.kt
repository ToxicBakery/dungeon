package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.world.World
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.provider

private class WorldManagerImpl : WorldManager {

    override suspend fun getWorldById(id: Int): World = World(0, "Overworld")

}

interface WorldManager {

    suspend fun getWorldById(id: Int):World

}

val worldManagerModule = Kodein.Module("worldManagerModule") {
    bind<WorldManager>() with provider {
        WorldManagerImpl()
    }
}
