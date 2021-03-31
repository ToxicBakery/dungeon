package com.toxicbakery.game.dungeon.map

import org.mapdb.Atomic

open class MapBaseFunctionality(
    private val mapSizeAtomic: Atomic.Integer,
    private val regionSizeAtomic: Atomic.Integer,
    private val mapIsFinalized: Atomic.Boolean
) {

    protected var isFinalized: Boolean
        get() = mapIsFinalized.get()
        set(value) {
            mapIsFinalized.set(value)
        }

    /**
     * Size of the table measuring the width (width == height, square map). Must be a power of 2.
     */
    protected var mapSize: Int
        get() = mapSizeAtomic.get().throwIfInvalid()
        set(value) {
            mapSizeAtomic.set(value)
        }

    /**
     * Size of regions in the map. Must be a power of 2.
     */
    protected var regionSize: Int
        get() = regionSizeAtomic.get().throwIfInvalid()
        set(value) = regionSizeAtomic.set(value)

    /**
     * Number of regions along the x or y axis
     */
    protected val regionCount: Int
        get() = mapSize / regionSize

    companion object {
        private fun Int.throwIfInvalid(): Int =
            if (this <= 0) error("Dimension not set!") else this
    }

}
