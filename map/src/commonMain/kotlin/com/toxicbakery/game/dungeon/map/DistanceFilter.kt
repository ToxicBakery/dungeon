package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.model.character.Location

data class DistanceFilter(
    val mapSize: Int,
    val maxDistance: Int
) {

    @Suppress("MagicNumber")
    fun nearby(
        locationA: Location,
        locationB: Location
    ): Boolean = locationA.worldId == locationB.worldId
            && locationA.distance(locationB, mapSize) <= maxDistance

}
