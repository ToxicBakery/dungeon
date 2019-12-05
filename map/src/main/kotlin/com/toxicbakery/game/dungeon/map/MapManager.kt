package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.model.character.Location
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.singleton
import org.mapdb.Atomic
import org.mapdb.DBMaker
import org.mapdb.HTreeMap
import kotlin.math.abs

/**
 * Size of the table measuring the width (width == height, square map). Must be a power of 2.
 */
private const val TABLE_MAP_SIZE_REF = "table_map_size_ref"

/**
 * Size of regions in the map. Must be a power of 2.
 */
private const val TABLE_REGION_SIZE_REF = "table_region_size_ref"

private const val TABLE_MAP = "map_"

/**id "com.github.gmazzo.buildconfig" version "1.6.1"
 * Root map (id = 0)
 */
private const val TABLE_MAP_ROOT = "${TABLE_MAP}_0"

class MapManager(
    private val mapSizeAtomic: Atomic.Integer,
    private val regionSizeAtomic: Atomic.Integer,
    private val map: HTreeMap<RegionLocation, Region>
) {

    /**
     * Size of the table measuring the width (width == height, square map). Must be a power of 2.
     */
    var mapSize: Int
        get() = mapSizeAtomic.get().throwIfInvalid()
        set(value) {
            mapSizeAtomic.set(value)
        }

    /**
     * Size of regions in the map. Must be a power of 2.
     */
    var regionSize: Int
        get() = regionSizeAtomic.get().throwIfInvalid()
        set(value) = regionSizeAtomic.set(value)

    /**
     * Number of regions along the x or y axis
     */
    private val regionCount: Int
        get() = mapSize / regionSize

    fun generateMap() {
        map.clear()

        if (!mapSize.isPositivePowerOfTwo() || !regionSize.isPositivePowerOfTwo())
            error("Map and Region sizes must be positive powers of 2")

        if (regionSize > mapSize) error("No..")

        val regionCount = regionCount
        (0 until regionCount)
            .flatMap { y -> (0 until regionCount).map { x -> RegionLocation(x, y) } }
            .forEach { regionLocation ->
                map[regionLocation] = Region(
                    byteArray = ByteArray(regionSize * regionSize) {
                        (regionLocation.x + regionLocation.y).toByte()
                    }
                )
            }
    }

    /**
     * Get a windowed view of the map using given display dimensions and a location.
     */
    fun drawWindow(window: Window): String {
        if (window.size > mapSize) error("Request ${window.size} exceeds world dimension $mapSize")
        if (window.size and 1 == 0) error("Request ${window.size} must be odd")

        val rCount = regionCount
        val topLeftRegionLocation = window.topLeftLocation.regionLocation
        val bottomRightRegionLocation = window.bottomRightLocation.regionLocation
        val topLeftInRegion = window.topLeftLocation.boundTo(regionSize)

        fun regionDistance(left: Int, right: Int): Int =
            if (left > right) distance((left - rCount) % rCount, right)
            else distance(left, right)

        val xDistance = regionDistance(topLeftRegionLocation.x, bottomRightRegionLocation.x)
        val yDistance = regionDistance(topLeftRegionLocation.y, bottomRightRegionLocation.y)

        // Strings are written left to right so iterate row by row collecting the byte arrays
        // so they are in order for drawing
        return (0..yDistance).map { y ->
            (0..xDistance).map { x ->
                RegionLocation.wrapped(
                    x = topLeftRegionLocation.x + x,
                    y = topLeftRegionLocation.y + y,
                    regionCount = regionCount
                ).region
            }.fold(RegionRow(), { acc, region -> acc + region })
        }.flatMap { regionRow -> regionRow.rows }
            .let { rows ->
                val bottomRightInRegion = Location(
                    x = topLeftInRegion.x + window.size,
                    y = topLeftInRegion.y + window.size
                )

                rows.filterIndexed { index, _ -> index >= topLeftInRegion.y && index < bottomRightInRegion.y }
                    .map { arr -> arr.slice(topLeftInRegion.x until bottomRightInRegion.x) }
                    .joinToString("\n", transform = {it.joinToString("")})
            }
    }

    private inner class RegionRow(
        val rows: List<ByteArray> = listOf()
    ) {

        operator fun plus(region: Region): RegionRow = (0 until regionSize)
            .map { row ->
                val start = row * regionSize
                val end = start + regionSize
                region.byteArray.sliceArray(start until end)
            }
            .let { incomingRows ->
                if (rows.isEmpty()) incomingRows
                else (0 until regionSize).map { i -> rows[i] + incomingRows[i] }
            }
            .let(::RegionRow)

    }

    private fun Location.boundTo(regionSize: Int): Location {
        val topLeftXNorm = x % regionSize
        val topLeftYNorm = y % regionSize

        return Location(
            x = if (topLeftXNorm < 0) regionSize + topLeftXNorm else topLeftXNorm,
            y = if (topLeftYNorm < 0) regionSize + topLeftYNorm else topLeftYNorm
        )
    }

    private fun fetchRegion(regionLocation: RegionLocation): Region = map.getValue(regionLocation)

    private val RegionLocation.region: Region
        get() = fetchRegion(this)

    private val Location.regionLocation: RegionLocation
        get() = regionSize.let { size ->
            RegionLocation.wrapped(
                regionCount = regionCount,
                x = if (x < 0) (mapSize + x) / size else x / size,
                y = if (y < 0) (mapSize + y) / size else y / size
            )
        }

    private fun Int.throwIfInvalid(): Int = if (this <= 0) error("Dimension not set!") else this

    private fun Int.isPositivePowerOfTwo(): Boolean = this > 0 && ((this and (this - 1)) == 0)

    companion object {

        /**
         * Distance between two points on a line.
         *
         * ```
         * D(AB)
         * ```
         */
        private fun distance(a: Int, b: Int): Int = abs(b - a)
    }

}

val mapManagerModule = Kodein.Module("mapManagerModule") {
    bind<MapManager>() with singleton {
        @Suppress("MagicNumber")
        val oneHundredMB = 100L * 1024L * 1024L
        val db = DBMaker.fileDB("dungeon.db")
            .closeOnJvmShutdown()
            .executorEnable()
            .fileMmapEnable()
            .allocateStartSize(oneHundredMB)
            .make()

        val tableSize = db.atomicInteger(TABLE_MAP_SIZE_REF)
            .createOrOpen()

        val regionSize = db.atomicInteger(TABLE_REGION_SIZE_REF)
            .createOrOpen()

        val map = db.hashMap(
            name = TABLE_MAP_ROOT,
            keySerializer = RegionLocation.Serializer,
            valueSerializer = Region.Serializer
        ).createOrOpen()

        MapManager(
            mapSizeAtomic = tableSize,
            regionSizeAtomic = regionSize,
            map = map
        )
    }
}
