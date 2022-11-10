package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.model.world.Location

internal class BoundingBox(
    val leftLocation: Location,
    val rightLocation: Location,
    mapSize: Int,
) {
    private val offsetX = if (leftLocation.x < rightLocation.x) 0 else leftLocation.x + (mapSize - leftLocation.x)
    private val offsetY = if (leftLocation.y < rightLocation.y) 0 else leftLocation.y + (mapSize - leftLocation.y)
    private val rightX = rightLocation.x + offsetX
    private val rightY = rightLocation.y + offsetY

    fun isBound(location: Location): Boolean {
        val pointX = if (location.x > rightLocation.x) location.x else location.x + offsetX
        val pointY = if (location.y > rightLocation.y) location.y else location.y + offsetY
        return pointX >= leftLocation.x && pointX <= rightX && pointY >= leftLocation.y && pointY <= rightY
    }
}
