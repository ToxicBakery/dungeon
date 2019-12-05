package com.toxicbakery.game.dungeon.map

import net.jpountz.xxhash.XXHash32
import org.mapdb.CC
import org.mapdb.DataInput2
import org.mapdb.DataOutput2
import kotlin.experimental.and
import kotlin.math.min
import kotlin.math.sqrt

class Region(
    val byteArray: ByteArray
) {

    private val regionSize: Int by lazy {
        sqrt(byteArray.size.toDouble()).toInt()
    }

    companion object {
        private const val COMPARE_MASK: Byte = 0xFF.toByte()

        @JvmStatic
        private val HASHER: XXHash32 = CC.HASH_FACTORY.hash32()
    }

    object Serializer :
        org.mapdb.Serializer<Region> {

        override fun serialize(out: DataOutput2, value: Region) {
            out.packInt(value.byteArray.size)
            out.write(value.byteArray)
        }

        override fun deserialize(input: DataInput2, available: Int): Region {
            val size = input.unpackInt()
            val byteArray = ByteArray(size)
            input.readFully(byteArray)
            return Region(byteArray)
        }

        override fun isTrusted(): Boolean = true

        @Suppress("WrongEqualsTypeParameter")
        override fun equals(first: Region, second: Region): Boolean =
            first.byteArray.contentEquals(second.byteArray)

        override fun hashCode(o: Region, seed: Int): Int =
            HASHER.hash(o.byteArray, 0, o.byteArray.size, seed)

        override fun compare(o1: Region, o2: Region): Int =
            if (o1.byteArray === o2.byteArray) 0
            else (0 until min(o1.byteArray.size, o2.byteArray.size))
                .map { i ->
                    val b1: Byte = o1.byteArray[i] and COMPARE_MASK
                    val b2: Byte = o2.byteArray[i] and COMPARE_MASK
                    b1 to b2
                }
                .map { (b1, b2) -> b1 - b2 }
                .firstOrNull { result -> result != 0 }
                ?: o1.byteArray.size - o2.byteArray.size

    }

}
