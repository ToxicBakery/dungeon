@file:Suppress("MagicNumber")

package com.toxicbakery.game.dungeon.model.character

import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable

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
)
