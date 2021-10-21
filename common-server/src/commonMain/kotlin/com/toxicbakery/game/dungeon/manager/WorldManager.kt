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
import com.toxicbakery.game.dungeon.model.Displayable
import com.toxicbakery.game.dungeon.model.animal.Animal
import com.toxicbakery.game.dungeon.model.character.Location
import com.toxicbakery.game.dungeon.model.character.Player
import com.toxicbakery.game.dungeon.model.character.npc.Npc
import com.toxicbakery.game.dungeon.model.creature.Creature
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.model.world.World
import com.toxicbakery.game.dungeon.persistence.store.GameClock
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider
import kotlin.math.abs

private class WorldManagerImpl(
    private val gameClock: GameClock,
    private val mapManager: MapManager,
    private val playerManager: PlayerManager
) : WorldManager {

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
        val windowDescription = windowDescriptionFor(player)
        val window = mapManager.drawWindow(windowDescription)
        val nearbyThings = nearbyThings(player)
        val center = WINDOW_SIZE / 2
        val mapSize = mapManager.mapSize()
        val halfMapSize = mapSize / 2

        nearbyThings.forEach { displayable ->
            val xDist = abs(displayable.location.x - player.location.x)
            val yDist = abs(displayable.location.y - player.location.y)

            // Account for wrap points and offset from center
            val rX = (if (xDist > halfMapSize) mapSize - displayable.location.x else xDist) + center
            val rY = (if (yDist > halfMapSize) mapSize - displayable.location.y else yDist) + center

            val xInBounds = rX in 0 until WINDOW_SIZE
            val yInBounds = rY in 0 until WINDOW_SIZE
            if (xInBounds && yInBounds) window.windowRows[rY][rX] = displayable.toMapLegend.byteRepresentation
        }

        return window
    }

    // TODO This should be moved to its own manager
    private suspend fun nearbyThings(player: Player): List<Displayable> {
        val npcsNearby = listOf<Displayable>().locationMapped()
        val animalsNearby = listOf<Displayable>(
            Animal(0, "", Location(1, 1), true),
            Animal(0, "", Location(0, 1), true),
            Animal(0, "", Location(1, 0), true),
            Animal(0, "", Location(2, 1), true),
            Animal(0, "", Location(1, 2), true)
        ).locationMapped()
        val creaturesNearby = listOf<Displayable>().locationMapped()
        val playersNearbyAndThisPlayer = playersNear(player)
            .plus(player)
            .locationMapped()

        // Stack nearby things in order of importance, first stacked is of lowest visual importance.
        return animalsNearby
            .plus(npcsNearby)
            .plus(playersNearbyAndThisPlayer)
            .plus(creaturesNearby)
            .values
            .toList()
    }

    private fun List<Displayable>.locationMapped(): Map<Location, Displayable> =
        associate { displayable -> displayable.location to displayable }

    private suspend fun playersNear(player: Player): List<Player> = playerManager.getPlayersNear(
        location = player.location,
        distanceFilter = DistanceFilter(mapManager.mapSize(), WINDOW_SIZE / 2 + 1)
    )

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

        private val dateFormat: DateFormat by lazy {
            PatternDateFormat("yyyy-MM-dd HH:mm")
        }

        private val Displayable.toMapLegend: MapLegend
            get() = when (this) {
                is Player -> MapLegend.PLAYER
                is Npc -> MapLegend.NPC
                is Animal -> if (this.passive) MapLegend.ANIMAL_PASSIVE else MapLegend.ANIMAL_AGGRESSIVE
                is Creature -> MapLegend.CREATURE
                else -> MapLegend.WTF
            }

        private fun windowDescriptionFor(player: Player) = WindowDescription(
            location = player.location,
            size = WINDOW_SIZE
        )
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
