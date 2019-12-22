package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.map.model.Window
import com.toxicbakery.game.dungeon.model.character.Location
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton
import kotlin.math.abs
import kotlin.math.ceil

private class MapManagerImpl(
    private val mapStore: MapStore
) : MapBaseFunctionality(
    mapSizeAtomic = mapStore.mapSizeAtomic,
    regionSizeAtomic = mapStore.regionSizeAtomic
), MapManager {

    private val RegionLocation.region: Region
        get() = fetchRegion(this)

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

    override fun mapSize(): Int = mapSize

    override fun drawWindow(windowDescription: WindowDescription): Window {
        windowDescription.validate()

        val topLeftRegionLocation = windowDescription.topLeftLocation.regionLocation
        val bottomRightRegionLocation = windowDescription.bottomRightLocation.regionLocationRoundUp
        val xRegionDistance = regionDistance(topLeftRegionLocation.x, bottomRightRegionLocation.x)
        val yRegionDistance = regionDistance(topLeftRegionLocation.y, bottomRightRegionLocation.y)
        val windowRows = (0..xRegionDistance)
            .flatMap { y ->
                (0..yRegionDistance)
                    .map { x ->
                        RelativeRegionLocation(x, y) to RegionLocation.wrapped(
                            regionCount = regionCount,
                            x = topLeftRegionLocation.x + x,
                            y = topLeftRegionLocation.y + y
                        ).region.byteArray
                    }
            }

        return Window(
            windowRows = paintRegions(
                windowDescription = windowDescription,
                regions = windowRows
            )
        )
    }

    override fun drawCompleteMap(): Window = (0 until regionCount)
        .flatMap { y -> (0 until regionCount).map { x -> RegionLocation(x, y) } }
        .map { regionLocation -> regionLocation to regionLocation.region.byteArray }
        .toMap()
        .let { regionMap: Map<RegionLocation, ByteArray> ->
            val rows = List(mapSize) { ByteArray(mapSize) }
            for (x in 0 until mapSize) {
                for (y in 0 until mapSize) {
                    val regionLocation = RegionLocation(x / regionSize, y / regionSize)
                    val region = regionMap.getValue(regionLocation)
                    val rX = x % regionSize
                    val rY = y % regionSize
                    rows[x][y] = region[rX * regionSize + rY]
                }
            }

            Window(rows.toList())
        }

    private fun paintRegions(
        windowDescription: WindowDescription,
        regions: List<Pair<RelativeRegionLocation, ByteArray>>
    ): List<ByteArray> {
        val rows = List(windowDescription.size) { ByteArray(windowDescription.size) }
        val visionTopLeftX = wrapWorldPosition(windowDescription.topLeftLocation.x)
        val visionTopLeftY = wrapWorldPosition(windowDescription.topLeftLocation.y)
        val offsetX = visionTopLeftX - windowDescription.topLeftLocation.regionLocation.x * regionSize
        val offsetY = visionTopLeftY - windowDescription.topLeftLocation.regionLocation.y * regionSize
        regions.forEach { (location, byteArray) ->
            val (rx, ry) = location
            byteArray.forEachIndexed { i, b ->
                val x = i / regionSize
                val y = i % regionSize
                val rx1 = (rx * regionSize + x) - offsetX
                val ry1 = (ry * regionSize + y) - offsetY
                val xInBound = rx1 >= 0 && rx1 < windowDescription.size
                val yInBound = ry1 >= 0 && ry1 < windowDescription.size
                if (xInBound && yInBound) rows[rx1][ry1] = b
            }
        }

        return rows
    }

    private fun fetchRegion(regionLocation: RegionLocation): Region =
        mapStore.map.getValue(regionLocation)

    private fun regionDistance(left: Int, right: Int): Int =
        if (left > right) distance((left - regionCount) % regionCount, right)
        else distance(left, right)

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
