package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.map.model.WindowMutable
import com.toxicbakery.game.dungeon.map.model.WindowRowMutable
import com.toxicbakery.game.dungeon.model.character.Location
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton
import kotlin.math.abs

private class MapManagerImpl(
    private val mapStore: MapStore
) : MapBaseFunctionality(
    mapSizeAtomic = mapStore.mapSizeAtomic,
    regionSizeAtomic = mapStore.regionSizeAtomic
), MapManager {

    override fun mapSize(): Int = mapSize

    override fun drawWindow(windowDescription: WindowDescription): WindowMutable {
        if (windowDescription.size > mapSize)
            error("Request ${windowDescription.size} exceeds world dimension $mapSize")
        if (windowDescription.size and 1 == 0)
            error("Request ${windowDescription.size} must be odd")

        val rCount = regionCount
        val topLeftRegionLocation = windowDescription.topLeftLocation.regionLocation
        val bottomRightRegionLocation = windowDescription.bottomRightLocation.regionLocation
        val topLeftInRegion = windowDescription.topLeftLocation.boundTo(regionSize)

        fun regionDistance(left: Int, right: Int): Int =
            if (left > right) distance((left - rCount) % rCount, right)
            else distance(left, right)

        val xRegionDistance = regionDistance(topLeftRegionLocation.x, bottomRightRegionLocation.x)
        val yRegionDistance = regionDistance(topLeftRegionLocation.y, bottomRightRegionLocation.y)

        // Strings are written left to right so iterate row by row collecting the byte arrays
        // so they are in order for drawing
        return (0..yRegionDistance).map { y ->
            (0..xRegionDistance).map { x ->
                RegionLocation.wrapped(
                    x = topLeftRegionLocation.x + x,
                    y = topLeftRegionLocation.y + y,
                    regionCount = regionCount
                ).region
            }.fold(RegionRow(), { acc, region -> acc + region })
        }.flatMap { regionRow -> regionRow.rows }
            .let { rows ->
                val bottomRightInRegion = Location(
                    x = topLeftInRegion.x + windowDescription.size,
                    y = topLeftInRegion.y + windowDescription.size
                )

                rows.filterIndexed { index, _ ->
                    index >= topLeftInRegion.y && index < bottomRightInRegion.y
                }.map { arr ->
                    WindowRowMutable(
                        row = arr.slice(topLeftInRegion.x until bottomRightInRegion.x).toMutableList()
                    )
                }.let { mutableRows -> WindowMutable(mutableRows.toMutableList()) }
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

    private fun fetchRegion(regionLocation: RegionLocation): Region =
        mapStore.map.getValue(regionLocation)

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


actual val mapManagerModule = Kodein.Module("mapManagerModule") {
    import(mapStoreModule)
    bind<MapManager>() with singleton {
        MapManagerImpl(
            mapStore = instance()
        )
    }
}
