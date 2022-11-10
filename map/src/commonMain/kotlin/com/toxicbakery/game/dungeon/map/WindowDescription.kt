package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.model.world.Location

data class WindowDescription(
    val location: Location,
    val size: Int
) {

    init {
        require(size > 0 && size % 2 == 1) { "Size must be a positive odd number: Size=$size" }
    }

    fun getTopLeftLocation(mapSize: Int): Location = Location(
        x = (location.x - size / 2).wrapTo(mapSize),
        y = (location.y - size / 2).wrapTo(mapSize)
    )

    fun getBottomRightLocation(mapSize: Int) = Location(
        x = (location.x + size / 2).wrapTo(mapSize),
        y = (location.y + size / 2).wrapTo(mapSize)
    )
}
