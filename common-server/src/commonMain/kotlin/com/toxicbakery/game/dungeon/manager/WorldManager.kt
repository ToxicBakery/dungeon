package com.toxicbakery.game.dungeon.manager

import com.soywiz.klock.DateFormat
import com.soywiz.klock.PatternDateFormat
import com.soywiz.klock.format
import com.toxicbakery.game.dungeon.model.world.World
import com.toxicbakery.game.dungeon.persistence.store.GameClock
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private class WorldManagerImpl(
    private val gameClock: GameClock
) : WorldManager {

    private val dateFormat: DateFormat by lazy {
        PatternDateFormat("yyyy-MM-dd HH:mm")
    }

    override suspend fun getWorldById(id: Int): World = World(0, "Overworld")

    override suspend fun getWorldTime(): String = dateFormat.format(gameClock.gameSeconds * MILLIS_PER_SECOND)

    companion object {
        private const val MILLIS_PER_SECOND: Long = 1000L
    }

}

interface WorldManager {

    suspend fun getWorldById(id: Int): World

    suspend fun getWorldTime(): String

}

val worldManagerModule = Kodein.Module("worldManagerModule") {
    bind<WorldManager>() with provider {
        WorldManagerImpl(
            gameClock = instance()
        )
    }
}
