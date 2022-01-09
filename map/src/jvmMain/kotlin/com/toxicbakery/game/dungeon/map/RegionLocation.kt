package com.toxicbakery.game.dungeon.map

import org.mapdb.DataInput2
import org.mapdb.DataOutput2

data class RegionLocation(
    val x: Int,
    val y: Int
) {

    /**
     * Ensure x and y coordinates are in bounds and wrap if necessary.
     */
    fun wrap(regionsWidth: Int): RegionLocation {
        val newX = wrapCoordinate(regionsWidth, x)
        val newY = wrapCoordinate(regionsWidth, y)
        return if (newX == x && newY == y) this
        else RegionLocation(newX, newY)
    }

    object Serializer : org.mapdb.Serializer<RegionLocation> {

        override fun serialize(out: DataOutput2, value: RegionLocation) {
            out.packInt(value.x)
            out.packInt(value.y)
        }

        override fun deserialize(input: DataInput2, available: Int): RegionLocation =
            RegionLocation(
                x = input.unpackInt(),
                y = input.unpackInt()
            )

        override fun isTrusted(): Boolean = true

    }

    companion object {
        fun wrapped(regionCount: Int, x: Int, y: Int): RegionLocation =
            RegionLocation(
                x = wrapCoordinate(
                    regionCount,
                    x
                ),
                y = wrapCoordinate(
                    regionCount,
                    y
                )
            )

        private fun wrapCoordinate(
            regionCount: Int,
            coordinate: Int
        ) = when {
            coordinate < 0 -> regionCount + coordinate
            coordinate >= regionCount -> coordinate - regionCount
            else -> coordinate
        }
    }

}
