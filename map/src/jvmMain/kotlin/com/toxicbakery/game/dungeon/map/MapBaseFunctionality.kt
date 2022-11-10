package com.toxicbakery.game.dungeon.map

import org.mapdb.Atomic

open class MapBaseFunctionality(
    private val mapSizeAtomic: Atomic.Integer,
) {

    /**
     * Size of the table measuring the width (width == height, square map). Must be a power of 2.
     */
    protected var mapSize: Int
        get() = mapSizeAtomic.get().throwIfInvalid("mapSize")
        set(value) {
            mapSizeAtomic.set(value)
        }

    companion object {
        private fun Int.throwIfInvalid(dimensionName: String): Int =
            if (this <= 0) throw DimensionNotSetException(dimensionName) else this
    }
}
