package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.map.DistanceFilter
import com.toxicbakery.game.dungeon.map.MapLegend
import com.toxicbakery.game.dungeon.map.MapManager
import com.toxicbakery.game.dungeon.map.WindowDescription
import com.toxicbakery.game.dungeon.map.model.Direction
import com.toxicbakery.game.dungeon.map.model.Window
import com.toxicbakery.game.dungeon.model.Lookable
import com.toxicbakery.game.dungeon.model.Lookable.*
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.model.world.Location
import com.toxicbakery.game.dungeon.model.world.LookLocation
import com.toxicbakery.game.dungeon.model.world.World
import com.toxicbakery.game.dungeon.persistence.store.GameClock
import kotlin.time.Duration.Companion.seconds
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private class WorldManagerImpl(
    private val gameClock: GameClock,
    private val mapManager: MapManager,
    private val playerManager: PlayerManager
) : WorldManager {

    override suspend fun getWorldById(id: Int): World = World(0, "Overworld")

    override suspend fun getWorldTime(): String = gameClock.gameSeconds.seconds.toIsoString()

    @Suppress("MagicNumber")
    override suspend fun getWindow(gameSession: GameSession): Window =
        getWindow(playerManager.getPlayerByGameSession(gameSession))

    override suspend fun getTravelLocation(
        player: Player,
        direction: Direction
    ): Location = player.location atDirectionOf direction

    @Suppress("MagicNumber")
    private suspend fun getWindow(player: Player): Window {
        val windowDescription = windowDescriptionFor(player)
        val nearbyThings = nearbyThings(
            player = player,
            distanceCap = windowDescription.size / 2 + 1
        ).locationMapped()
        return mapManager.drawWindow(windowDescription) { mapOverlay ->
            nearbyThings.forEach { (location, displayable) ->
                mapOverlay.addOverlayItem(
                    location,
                    displayable.toMapLegend
                )
            }
        }
    }

    // TODO This should be moved to its own manager
    private suspend fun nearbyThings(
        player: Player,
        distanceCap: Int,
    ): List<Lookable> {
        val npcsNearby = listOf<Lookable>()
        val animalsNearby = listOf<Lookable>(
            Animal(1, "1, 1", Location(1, 1), true),
            Animal(2, "0, 1", Location(0, 1), true),
            Animal(3, "1, 0", Location(1, 0), true),
            Animal(4, "2, 1", Location(2, 1), true),
            Animal(5, "1, 2", Location(1, 2), true)
        )
        val creaturesNearby = listOf<Lookable>()
        val playersNearby = playersNear(player)

        // Stack nearby things in order of importance, first stacked is of lowest visual importance.
        return animalsNearby
            .plus(npcsNearby)
            .plus(playersNearby)
            .plus(creaturesNearby)
            .filter { lookable ->
                lookable.location.distance(player.location, mapManager.mapSize()) <= distanceCap
            }
    }

    override suspend fun look(
        player: Player,
        direction: Direction?
    ): LookLocation {
        val targetLocation =
            if (direction == null) player.location
            else player.location atDirectionOf direction

        return LookLocation(
            location = targetLocation,
            lookables = nearbyThings(player, 0).minus(player),
            mapLegendByte = mapManager.drawLocation(
                windowDescription = WindowDescription(
                    location = targetLocation,
                    size = 1
                ),
            ),
            world = getWorldById(targetLocation.worldId)
        )
    }

    private fun List<Lookable>.locationMapped(): Map<Location, Lookable> =
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

    private infix fun Location.atDirectionOf(
        direction: Direction
    ) = when (direction) {
        Direction.NORTH -> copy(y = wrapped(mapManager.mapSize(), y - 1))
        Direction.SOUTH -> copy(y = wrapped(mapManager.mapSize(), y + 1))
        Direction.WEST -> copy(x = wrapped(mapManager.mapSize(), x - 1))
        Direction.EAST -> copy(x = wrapped(mapManager.mapSize(), x + 1))
    }

    companion object {
        private const val WINDOW_SIZE = 9

        private val Lookable.toMapLegend: MapLegend
            get() = when (this) {
                is Player -> MapLegend.PLAYER
                is Npc -> MapLegend.NPC
                is Animal -> if (isPassive) MapLegend.ANIMAL_PASSIVE else MapLegend.ANIMAL_AGGRESSIVE
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

    suspend fun look(
        player: Player,
        direction: Direction?
    ): LookLocation
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
