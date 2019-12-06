@file:Suppress("MagicNumber")

package com.toxicbakery.game.dungeon.model.character

import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Representation of the characters current location in the world.
 * @param x coordinate of the player in the world
 * @param y coordinate of the player in the world
 * @param worldId identifier of what world the player is currently located
 */
@Serializable
data class Location(
    @SerialId(1)
    val x: Int = 0,
    @SerialId(2)
    val y: Int = 0,
    @SerialId(3)
    val worldId: Int = 0
) {

    fun distance(
        location: Location,
        mapSize: Int
    ): Int {
        if (worldId != location.worldId) error("Attempt to calculate distance between worlds.")
        val x = wrappedDistance(x, location.x, mapSize).pow(2)
        val y = wrappedDistance(y, location.y, mapSize).pow(2)
        return sqrt((x + y).toDouble()).toInt()
    }

    private fun wrappedDistance(
        a: Int,
        b: Int,
        mapSize: Int
    ): Int = abs(a - b).let { r ->
        if (r > mapSize / 2) mapSize - r
        else a - b
    }

    private fun Int.pow(pow: Int): Int =
        (0 until pow - 1).fold(this, { acc, _ -> acc * this })

}
