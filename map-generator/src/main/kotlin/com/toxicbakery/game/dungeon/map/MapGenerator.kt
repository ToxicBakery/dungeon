package com.toxicbakery.game.dungeon.map

import com.sudoplay.joise.module.Module
import com.sudoplay.joise.module.ModuleAutoCorrect
import com.sudoplay.joise.module.ModuleBasisFunction
import com.sudoplay.joise.module.ModuleFractal
import com.toxicbakery.game.dungeon.map.MapGenerator.MapConfig
import com.toxicbakery.game.dungeon.map.preview.MapPreviewer
import kotlin.random.Random
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider
import org.mapdb.HTreeMap

@Suppress("MagicNumber", "SameParameterValue")
private class MapGeneratorImpl(
    private val mapStore: MapStore
) : MapBaseFunctionality(
    mapSizeAtomic = mapStore.mapSizeAtomic,
),
    MapGenerator {

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
            ac.samples = 10_000
            ac.calculate()

            return ac
        }

    private val map: HTreeMap<RegionLocation, Byte>
        get() = mapStore.map

    @Suppress("NestedBlockDepth")
    override fun generateMap(
        mapConfig: MapConfig,
        previewers: List<MapPreviewer>
    ) {
        try {
            if (mapSize == mapConfig.mapSize) {
                println("Skipping map generation; map meets requested configuration.")
                return
            }

            if (!mapConfig.mapSize.isPositivePowerOfTwo()) {
                error("Map sizes must be positive powers of 2")
            }
        } catch (_: DimensionNotSetException) {
            // No map previously generated, starting with clean database.
            println("Creating new map DB")
        }

        mapSize = mapConfig.mapSize

        val mapData = TerrainGenerator(
            moduleFactory = { sampleGenerator },
            configuration = TerrainConfiguration(mapSize = mapSize)
        ).generate()

        // Generate previews
        previewers.forEach { previewer -> previewer.preview(mapSize, mapData) }

        // Copy generated map into db
        populateDb(mapData)
    }

    /**
     * Copy map data into the database splitting the map into regions.
     */
    private fun populateDb(mapData: MapData) {
        // Reset the map in the database
        map.clear()

        // Fill the map with regions. Reminder that source data is in y,x format and thus has to be rotated
        // when copying to the map database such that regions and their sub data are properly populated.
        for (y in 0 until mapSize) {
            for (x in 0 until mapSize) {
                map[RegionLocation(x, y)] = mapData[y * mapSize + x].byteRepresentation
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
    )
}

val mapGeneratorModule = DI.Module("mapGeneratorModule") {
    bind<MapGenerator>() with provider {
        MapGeneratorImpl(
            mapStore = instance()
        )
    }
}
