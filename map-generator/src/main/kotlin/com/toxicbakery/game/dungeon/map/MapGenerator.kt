package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.map.MapGenerator.MapConfig
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider
import org.mapdb.HTreeMap

private class MapGeneratorImpl(
    private val mapStore: MapStore
) : MapBaseFunctionality(
    mapSizeAtomic = mapStore.mapSizeAtomic,
    regionSizeAtomic = mapStore.regionSizeAtomic
), MapGenerator {

    private val map: HTreeMap<RegionLocation, Region>
        get() = mapStore.map

    override fun generateMap(mapConfig: MapConfig) {
        // Avoid regen
        if (mapSize == mapConfig.mapSize && regionSize == mapConfig.regionSize) return
        mapSize = mapConfig.mapSize
        regionSize = mapConfig.regionSize

        if (!mapSize.isPositivePowerOfTwo() || !regionSize.isPositivePowerOfTwo())
            error("Map and Region sizes must be positive powers of 2")

        if (regionSize >= mapSize) error("No..")

        map.clear()

        val regionCount = regionCount
        (0 until regionCount)
            .flatMap { y ->
                (0 until regionCount).map { x ->
                    RegionLocation(
                        x,
                        y
                    )
                }
            }
            .forEach { regionLocation ->
                map[regionLocation] = Region(
                    byteArray = ByteArray(regionSize * regionSize) {
                        (regionLocation.x + regionLocation.y).toByte()
                    }
                )
            }
    }

    companion object {
        private fun Int.isPositivePowerOfTwo(): Boolean =
            this > 0 && ((this and (this - 1)) == 0)
    }

}

interface MapGenerator {

    fun generateMap(mapConfig: MapConfig)

    data class MapConfig(
        val mapSize: Int,
        val regionSize: Int
    )

}

val mapGeneratorModule = Kodein.Module("mapGeneratorModule") {
    bind<MapGenerator>() with provider {
        MapGeneratorImpl(
            mapStore = instance()
        )
    }
}
