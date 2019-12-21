package com.toxicbakery.game.dungeon.map

import com.sudoplay.joise.module.Module
import com.sudoplay.joise.module.ModuleAutoCorrect
import com.sudoplay.joise.module.ModuleBasisFunction
import com.sudoplay.joise.module.ModuleFractal
import com.toxicbakery.game.dungeon.map.MapGenerator.MapConfig
import com.toxicbakery.game.dungeon.map.preview.MapPreviewer
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider
import org.mapdb.HTreeMap
import kotlin.random.Random

@Suppress("MagicNumber")
private class MapGeneratorImpl(
    private val mapStore: MapStore
) : MapBaseFunctionality(
    mapSizeAtomic = mapStore.mapSizeAtomic,
    regionSizeAtomic = mapStore.regionSizeAtomic
), MapGenerator {

    private val sampleGenerator: Module
        get() {
            val gen = ModuleFractal()
            gen.setAllSourceBasisTypes(ModuleBasisFunction.BasisType.SIMPLEX)
            gen.setAllSourceInterpolationTypes(ModuleBasisFunction.InterpolationType.QUINTIC)
            gen.numOctaves = 6
            gen.frequency = 1.25
            gen.type = ModuleFractal.FractalType.MULTI
            gen.seed = Random.nextLong()

            val ac = ModuleAutoCorrect()
            ac.setSource(gen)
            ac.setRange(0.0, 1.0)
            ac.samples = 10000
            ac.calculate()

            return ac
        }

    private val map: HTreeMap<RegionLocation, Region>
        get() = mapStore.map

    @Suppress("NestedBlockDepth")
    override fun generateMap(
        mapConfig: MapConfig,
        previewers: List<MapPreviewer>
    ) {
        mapSize = mapConfig.mapSize
        regionSize = mapConfig.regionSize

        if (mapSize == mapConfig.mapSize && regionSize == mapConfig.regionSize)
            println("Skipping map generation; map meets requested configuration.")

        if (mapSize <= regionSize) error("Map size must be larger than region size")

        if (!mapSize.isPositivePowerOfTwo() || !regionSize.isPositivePowerOfTwo())
            error("Map and Region sizes must be positive powers of 2")

        val mapData = TerrainGenerator(
            moduleFactory = { sampleGenerator },
            configuration = TerrainConfiguration(mapSize = mapSize)
        ).generate()

        map.clear()

        // Generate previews
        previewers.forEach { previewer -> previewer.preview(mapSize, mapData) }

        // Fill the map with regions
        for (x in 0 until mapSize / regionSize)
            for (y in 0 until mapSize / regionSize)
                map[RegionLocation(x, y)] = Region(ByteArray(regionSize * regionSize))

        // Copy map data into the regions
        for (x in 0 until mapSize) {
            for (y in 0 until mapSize) {
                val regionLocation = RegionLocation(x / regionSize, y / regionSize)
                val regionData = map.getValue(regionLocation).byteArray
                val mapLegend = mapData[x * mapSize + y]
                val x0 = x % regionSize
                val y0 = y % regionSize
                regionData[x0 * regionSize + y0] = mapLegend.byteRepresentation
                map[regionLocation] = Region(regionData)
            }
        }
    }

    companion object {
        private fun Int.isPositivePowerOfTwo(): Boolean =
            this > 0 && ((this and (this - 1)) == 0)
    }

}

interface MapGenerator {

    fun generateMap(
        mapConfig: MapConfig,
        previewers: List<MapPreviewer> = listOf()
    )

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
