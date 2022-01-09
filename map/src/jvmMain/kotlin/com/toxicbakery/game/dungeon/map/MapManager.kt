package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.map.model.Window
import com.toxicbakery.game.dungeon.model.character.Location
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sqrt

private class MapManagerImpl(
    private val mapStore: MapStore
) : MapBaseFunctionality(
    mapSizeAtomic = mapStore.mapSizeAtomic,
    regionSizeAtomic = mapStore.regionSizeAtomic
), MapManager {

    private val RegionLocation.region: Region
        get() = fetchRegion(this)

    private val RegionLocation.byteArray: ByteArray
        get() = region.byteArray

    private val Location.regionLocation: RegionLocation
        get() = regionSize.let { size ->
            RegionLocation.wrapped(
                regionCount = regionCount,
                x = wrapWorldPosition(x) / size,
                y = wrapWorldPosition(y) / size
            )
        }

    private val Location.regionLocationRoundUp: RegionLocation
        get() = regionSize.let { size ->
            RegionLocation.wrapped(
                regionCount = regionCount,
                x = ceil(wrapWorldPosition(x) / size.toDouble()).toInt(),
                y = ceil(wrapWorldPosition(y) / size.toDouble()).toInt()
            )
        }

    /**
     * Get all region locations in order left to right with the region data extracted.
     */
    private val allRegions: List<Pair<RegionLocation, Region>>
        get() = (0 until regionCount)
            .flatMap { y -> (0 until regionCount).map { x -> RegionLocation(x, y) } }
            .map { regionLocation -> regionLocation to regionLocation.region }

    override fun mapSize(): Int = mapSize

    override fun drawWindow(windowDescription: WindowDescription): Window {
//        val regionX = windowDescription.location.x / regionSize
//        val regionY = windowDescription.location.y / regionSize
//        val regionBytes = RegionLocation(regionX, regionY).region.byteArray
//        val windowRows = (0..regionSize).map { ByteArray(regionSize) }
//
//        for (x in 0 until regionSize) {
//            for (y in 0 until regionSize) {
//                windowRows[x][y] = regionBytes[y * regionSize + x]
//            }
//        }
//
//        return Window(
//            windowRows = windowRows
//        )
        windowDescription.validate()

        val topLeftRegionLocation = windowDescription.getTopLeftLocation(mapSize).regionLocation
        val bottomRightRegionLocation = windowDescription.getBottomRightLocation(mapSize).regionLocationRoundUp
        val xRegionDistance = regionDistance(topLeftRegionLocation.x, bottomRightRegionLocation.x)
        val yRegionDistance = regionDistance(topLeftRegionLocation.y, bottomRightRegionLocation.y)
        println("top left region: $topLeftRegionLocation")
        println("bottom right region: $bottomRightRegionLocation")
        val windowRows = (0..xRegionDistance)
            .flatMap { a ->
                (0..yRegionDistance)
                    .map { b ->
                        fun wrapRegion(a: Int) = if (a < regionCount) a else a - regionCount
                        val x = wrapRegion(b + topLeftRegionLocation.x)
                        val y = wrapRegion(a + topLeftRegionLocation.y)
                        println("Getting region $x, $y")
                        RegionLocation(
                            x = x,
                            y = y
                        )
                    }
            }

        return Window(
            windowRows = paintRegions(
                windowDescription = windowDescription,
                regionLocations = windowRows
            )
        )
    }

    override fun drawCompleteMap(): Window = allRegions
        .toMap()
        .let { regionMap: Map<RegionLocation, Region> ->
            val rows = List(mapSize) { ByteArray(mapSize) }
            for (x in 0 until mapSize) {
                for (y in 0 until mapSize) {
                    val regionLocation = RegionLocation(x / regionSize, y / regionSize)
                    val region = regionMap.getValue(regionLocation).byteArray
                    val rX = x % regionSize
                    val rY = y % regionSize
                    rows[x][y] = region[rY * regionSize + rX]
                }
            }

            Window(rows)
        }

    private fun paintRegions(
        windowDescription: WindowDescription,
        regionLocations: List<RegionLocation>
    ): List<ByteArray> {
        val rC = sqrt(regionLocations.size.toDouble()).toInt()
        val rows = List(windowDescription.size) { ByteArray(windowDescription.size) }
        val topLeftRegion = regionLocations.first()
        val topLeftRegionLocation = topLeftRegion.let { regionLocation ->
            Location(regionLocation.x * regionSize, regionLocation.y * regionSize)
        }

        val topLeftWindowLocation = windowDescription.getTopLeftLocation(mapSize)

        regionLocations.forEachIndexed { index, regionLocation ->
            val row = index / rC
            val column = index % rC
            val byteArray = regionLocation.byteArray

            // windowRows[x][y] = regionBytes[y * regionSize + x]
            for (y in 0 until regionSize) {
                for (x in 0 until regionSize) {
                    val wX = (topLeftRegionLocation.x + x).wrapTo(mapSize)
                    val wY = (topLeftRegionLocation.y + y).wrapTo(mapSize)
                    if (wX >= topLeftWindowLocation.x)

                    val rX = x % regionSize
                    val rY = y % regionSize
                    rows[row * regionSize + x][column * regionSize + y] = byteArray[rY * regionSize + rX]
                }
            }
        }

//        val rows = List(windowDescription.size) { ByteArray(windowDescription.size) }
//        val topLeftLocation = windowDescription.topLeftLocation
//        val topLeftRegionLocation = topLeftLocation.regionLocation
//        val visionTopLeftX = wrapWorldPosition(topLeftLocation.x)
//        val visionTopLeftY = wrapWorldPosition(topLeftLocation.y)
//        val offsetX = visionTopLeftX - topLeftRegionLocation.x * regionSize
//        val offsetY = visionTopLeftY - topLeftRegionLocation.y * regionSize
//        regionLocations.forEach { regionLocation ->
//            val (rx, ry) = regionLocation
//            //windowRows[x][y] = regionBytes[y * regionSize + x]
//            regionLocation.byteArray.forEachIndexed { i, b ->
//                val x = i % regionSize
//                val y = i / regionSize
//                val rx1 = (rx * regionSize + x) - offsetX
//                val ry1 = (ry * regionSize + y) - offsetY
//                val xInBound = rx1 >= 0 && rx1 < windowDescription.size
//                val yInBound = ry1 >= 0 && ry1 < windowDescription.size
//                if (xInBound && yInBound) rows[rx1][ry1] = b
//            }
//        }

        return rows
    }

    private fun fetchRegion(regionLocation: RegionLocation): Region =
        mapStore.map.getValue(regionLocation)

    private fun regionDistance(left: Int, right: Int): Int = when {
        left == right -> 0
        left < right -> distance(left, right)
        else -> distance((left - regionCount) % regionCount, right)
    }

    private fun WindowDescription.validate() {
        if (size > mapSize)
            error("Request $size exceeds world dimension $mapSize")
        if (size and 1 == 0)
            error("Request $size must be odd")
    }

    private fun wrapWorldPosition(pos: Int) = if (pos < 0) mapSize + pos else pos

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

private data class RelativeRegionLocation(val x: Int, val y: Int)

actual val mapManagerModule = Kodein.Module("mapManagerModule") {
    import(mapStoreModule)
    bind<MapManager>() with singleton {
        MapManagerImpl(
            mapStore = instance()
        )
    }
}
