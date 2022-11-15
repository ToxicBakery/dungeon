@file:Suppress("MagicNumber")

package com.toxicbakery.game.dungeon.model.world

import com.toxicbakery.game.dungeon.model.world.Distance.Companion.wrappedDistanceLine
import kotlin.math.sqrt
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/**
 * Representation of the characters current location in the world.
 * @param x coordinate of the player in the world
 * @param y coordinate of the player in the world
 * @param worldId identifier of what world the player is currently located
 */
@Serializable
data class Location(
    @ProtoNumber(1)
    val x: Int = 0,
    @ProtoNumber(2)
    val y: Int = 0,
    @ProtoNumber(3)
    val worldId: String = "overworld"
) {

    fun distance(
        location: Location,
        mapSize: Int
    ): Int {
        if (worldId != location.worldId) error("Attempted to calculate distance between different worlds.")
        if (this == location) return 0
        val x = wrappedDistanceLine(x, location.x, mapSize).pow2()
        val y = wrappedDistanceLine(y, location.y, mapSize).pow2()
        return sqrt((x + y).toDouble()).toInt()
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Int.pow2(): Int = this * this
}
