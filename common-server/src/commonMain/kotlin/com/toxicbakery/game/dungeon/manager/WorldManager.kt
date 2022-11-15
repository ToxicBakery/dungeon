package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.map.DistanceFilter
import com.toxicbakery.game.dungeon.map.MapLegend
import com.toxicbakery.game.dungeon.map.MapManager
import com.toxicbakery.game.dungeon.map.WindowDescription
import com.toxicbakery.game.dungeon.map.model.Direction
import com.toxicbakery.game.dungeon.map.model.Window
import com.toxicbakery.game.dungeon.model.ILookable
import com.toxicbakery.game.dungeon.model.Locatable
import com.toxicbakery.game.dungeon.model.Lookable
import com.toxicbakery.game.dungeon.model.Lookable.*
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.model.world.Location
import com.toxicbakery.game.dungeon.model.world.LookLocation
import com.toxicbakery.game.dungeon.model.world.World
import com.toxicbakery.game.dungeon.persistence.store.GameClock
import com.toxicbakery.game.dungeon.util.secondsToDays
import com.toxicbakery.game.dungeon.util.secondsToMonths
import com.toxicbakery.game.dungeon.util.secondsToYears
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

private class WorldManagerImpl(
    private val gameClock: GameClock,
) : WorldManager {

    // TODO Create world database
    override suspend fun getWorldById(id: String): World = World("overworld", "Overworld")

    override suspend fun getWorldTime(): String = gameClock.gameSeconds.let { seconds ->
        val year = seconds.secondsToYears
        val month = seconds.secondsToMonths
        val day = seconds.secondsToDays
        "It is $month $day, $year"
    }
}

interface WorldManager {

    suspend fun getWorldById(id: String): World

    suspend fun getWorldTime(): String
}

val worldManagerModule = DI.Module("worldManagerModule") {
    bind<WorldManager>() with provider {
        WorldManagerImpl(
            gameClock = instance(),
        )
    }
}
