package com.toxicbakery.game.dungeon.map

import org.mapdb.DataInput2
import org.mapdb.DataOutput2

data class RegionLocation(
    val x: Int,
    val y: Int
) {

    object Serializer :
        org.mapdb.Serializer<RegionLocation> {

        override fun serialize(out: DataOutput2, value: RegionLocation) {
            out.writeInt(value.x)
            out.writeInt(value.y)
        }

        override fun deserialize(input: DataInput2, available: Int): RegionLocation =
            RegionLocation(
                x = input.readInt(),
                y = input.readInt()
            )

        override fun isTrusted(): Boolean = true

    }

}
