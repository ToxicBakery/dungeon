package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.commonApplicationKodein
import com.toxicbakery.game.dungeon.map.MapManager
import com.toxicbakery.game.dungeon.map.WindowDescription
import com.toxicbakery.game.dungeon.map.model.Direction
import com.toxicbakery.game.dungeon.model.ILookable
import com.toxicbakery.game.dungeon.model.Lookable
import com.toxicbakery.game.dungeon.model.world.Location
import com.toxicbakery.game.dungeon.model.world.LookLocation
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

private class LookManagerImpl(
    override val di: DI = commonApplicationKodein
) : LookManager, DIAware {

    private val mapManager: MapManager by di.instance()
    private val nearbyManager: NearbyManager by di.instance()
    private val worldManager: WorldManager by di.instance()

    override suspend fun look(
        lookable: ILookable,
        direction: Direction?
    ): LookLocation {
        val targetLocation =
            if (direction == null) lookable.location
            else lookable.location atDirectionOf direction

        return LookLocation(
            location = targetLocation,
            lookables = nearbyManager.getNearbyLookables(targetLocation, 0)
                .minus(lookable)
                .map { it as Lookable },
            mapLegendByte = mapManager.drawLocation(
                windowDescription = WindowDescription(
                    location = targetLocation,
                    size = 1
                ),
            ),
            world = worldManager.getWorldById(targetLocation.worldId)
        )
    }

    private infix fun Location.atDirectionOf(
        direction: Direction
    ) = when (direction) {
        Direction.NORTH -> copy(y = wrapped(mapManager.mapSize(), y - 1))
        Direction.SOUTH -> copy(y = wrapped(mapManager.mapSize(), y + 1))
        Direction.WEST -> copy(x = wrapped(mapManager.mapSize(), x - 1))
        Direction.EAST -> copy(x = wrapped(mapManager.mapSize(), x + 1))
    }

    private fun wrapped(
        mapSize: Int,
        position: Int
    ) = when {
        position < 0 -> mapSize - 1
        position >= mapSize -> 0
        else -> position
    }
}

interface LookManager {
    suspend fun look(
        lookable: ILookable,
        direction: Direction?
    ): LookLocation
}

val lookManagerModule = DI.Module("lookManagerModule") {
    bind<LookManager>() with provider { LookManagerImpl() }
}
