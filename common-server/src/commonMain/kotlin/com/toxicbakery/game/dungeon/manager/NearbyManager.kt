package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.map.DistanceFilter
import com.toxicbakery.game.dungeon.map.MapManager
import com.toxicbakery.game.dungeon.model.ILookable
import com.toxicbakery.game.dungeon.model.world.Location
import com.toxicbakery.game.dungeon.persistence.npc.NpcDatabase
import com.toxicbakery.game.dungeon.persistence.player.PlayerDatabase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

private class NearbyManagerImpl(
    private val mapManager: MapManager,
    private val npcDatabase: NpcDatabase,
    private val playerDatabase: PlayerDatabase,
) : NearbyManager {

    private val mapSize: Int
        get() = mapManager.mapSize()

    override suspend fun getNearbyLookables(
        source: ILookable
    ): List<ILookable> = getNearbyNpcs(source)
        .plus(getNearbyPlayers(source))

    override suspend fun getNearbyLookables(
        target: Location,
        distance: Int
    ): List<ILookable> = getNearbyNpcs(target, distance)
        .plus(getNearbyPlayers(target, distance))

    private suspend fun getNearbyNpcs(
        location: Location,
        distance: Int,
    ): List<ILookable> = npcDatabase.getNpcsNear(
        location = location,
        distanceFilter = DistanceFilter(mapSize, distance)
    )

    private suspend fun getNearbyNpcs(
        source: ILookable
    ): List<ILookable> = getNearbyNpcs(source.location, 5)

    private suspend fun getNearbyPlayers(
        location: Location,
        distance: Int,
    ): List<ILookable> = playerDatabase.getPlayersNear(
        location = location,
        distanceFilter = DistanceFilter(mapSize, distance)
    )

    private suspend fun getNearbyPlayers(
        source: ILookable
    ): List<ILookable> = getNearbyPlayers(source.location, 5)
}

interface NearbyManager {
    suspend fun getNearbyLookables(
        source: ILookable,
    ): List<ILookable>

    suspend fun getNearbyLookables(
        target: Location,
        distance: Int,
    ): List<ILookable>
}

val nearbyManagerModule = DI.Module("nearbyManagerModule") {
    bind<NearbyManager>() with singleton {
        NearbyManagerImpl(
            mapManager = instance(),
            npcDatabase = instance(),
            playerDatabase = instance(),
        )
    }
}
