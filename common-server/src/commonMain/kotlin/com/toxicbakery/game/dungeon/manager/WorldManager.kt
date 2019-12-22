package com.toxicbakery.game.dungeon.manager

import com.soywiz.klock.DateFormat
import com.soywiz.klock.PatternDateFormat
import com.soywiz.klock.format
import com.toxicbakery.game.dungeon.map.DistanceFilter
import com.toxicbakery.game.dungeon.map.MapLegend
import com.toxicbakery.game.dungeon.map.MapManager
import com.toxicbakery.game.dungeon.map.WindowDescription
import com.toxicbakery.game.dungeon.map.model.Direction
import com.toxicbakery.game.dungeon.map.model.Window
import com.toxicbakery.game.dungeon.model.character.Location
import com.toxicbakery.game.dungeon.model.character.Player
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.model.world.World
import com.toxicbakery.game.dungeon.persistence.store.GameClock
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private class WorldManagerImpl(
    private val gameClock: GameClock,
    private val mapManager: MapManager,
    private val playerManager: PlayerManager
) : WorldManager {

    private val dateFormat: DateFormat by lazy {
        PatternDateFormat("yyyy-MM-dd HH:mm")
    }

    override suspend fun getWorldById(id: Int): World = World(0, "Overworld")

    override suspend fun getWorldTime(): String = dateFormat.format(gameClock.gameSeconds * MILLIS_PER_SECOND)

    @Suppress("MagicNumber")
    override suspend fun getWindow(gameSession: GameSession): Window =
        getWindow(playerManager.getPlayerByGameSession(gameSession))

    override suspend fun getTravelLocation(
        player: Player,
        direction: Direction
    ): Location = player.location.travel(direction)

    @Suppress("MagicNumber")
    private suspend fun getWindow(player: Player): Window {
        val players = playerManager.getPlayersNear(
            location = player.location,
            distanceFilter = DistanceFilter(mapManager.mapSize(), WINDOW_SIZE / 2 + 1)
        )

        val windowDescription = WindowDescription(
            location = player.location,
            size = WINDOW_SIZE
        )

        val locations = players.map { p ->
            Location(
                x = wrapped(WINDOW_SIZE, p.location.x - windowDescription.topLeftLocation.x),
                y = wrapped(WINDOW_SIZE, p.location.y - windowDescription.topLeftLocation.y)
            )
        }

        return mapManager.drawWindow(windowDescription)
//            .apply {
//                windowRows.forEachIndexed { index, windowRow ->
//                    locations.forEach { location ->
//                        if (location.y == index && location.x < WINDOW_SIZE)
//                            windowRow[location.x] = MapLegend.PLAYER.byteRepresentation
//                    }
//                }
//            }
    }

    private fun wrapped(
        mapSize: Int,
        position: Int
    ) = when {
        position < 0 -> mapSize - 1
        position >= mapSize -> 0
        else -> position
    }

    private fun Location.travel(
        direction: Direction
    ) = when (direction) {
        Direction.NORTH -> copy(y = wrapped(mapManager.mapSize(), y - 1))
        Direction.SOUTH -> copy(y = wrapped(mapManager.mapSize(), y + 1))
        Direction.WEST -> copy(x = wrapped(mapManager.mapSize(), x - 1))
        Direction.EAST -> copy(x = wrapped(mapManager.mapSize(), x + 1))
    }

    companion object {
        private const val MILLIS_PER_SECOND: Long = 1000L
        private const val WINDOW_SIZE = 7
    }

}

interface WorldManager {

    suspend fun getWorldById(id: Int): World

    suspend fun getWorldTime(): String

    suspend fun getWindow(gameSession: GameSession): Window

    /**
     * Get the travel location or throw if travel not possible.
     */
    suspend fun getTravelLocation(
        player: Player,
        direction: Direction
    ): Location

}

val worldManagerModule = Kodein.Module("worldManagerModule") {
    bind<WorldManager>() with provider {
        WorldManagerImpl(
            gameClock = instance(),
            mapManager = instance(),
            playerManager = instance()
        )
    }
}
