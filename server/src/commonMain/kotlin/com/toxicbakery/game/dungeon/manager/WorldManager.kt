package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.store.GameClock
import com.toxicbakery.game.dungeon.world.World
import io.ktor.util.date.GMTDate
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private class WorldManagerImpl(
    private val gameClock: GameClock
) : WorldManager {

    override suspend fun getWorldById(id: Int): World = World(0, "Overworld")

    override suspend fun getWorldTime(): String {
        val date = GMTDate(gameClock.gameSeconds * MILLIS_PER_SECOND)
        val dateString = "${date.month.name} ${date.dayOfMonth}, ${date.year}"
        val timeString = "${date.hours}:${date.minutes}"
        return "$dateString $timeString"
    }

    companion object {
        private const val MILLIS_PER_SECOND: Long = 1000L
    }

}

interface WorldManager {

    suspend fun getWorldById(id: Int):World

    suspend fun getWorldTime(): String

}

val worldManagerModule = Kodein.Module("worldManagerModule") {
    bind<WorldManager>() with provider {
        WorldManagerImpl(
            gameClock = instance()
        )
    }
}
