package com.toxicbakery.game.dungeon.map

import com.sudoplay.joise.module.Module
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class TerrainGenerator(
    private val moduleFactory: () -> Module,
    private val configuration: TerrainConfiguration = TerrainConfiguration()
) {

    private val width: Int = configuration.mapSize
    private val height: Int = configuration.mapSize

    init {
        if (!configuration.mapSize.isPowerOfTwo()) error("Size must be a power of 2")
    }

    fun generate(): MapData {
        val map: Array<MapLegend> = Array(width * height) { MapLegend.NULL }
        val coordinatePoints: Array<Point> = Array(width * height) { i -> point(i / width, i % width) }
        generateBaseLand(map, coordinatePoints)
        drawBeaches(map, coordinatePoints)
        drawForest(map, coordinatePoints)
        drawDesert(map, coordinatePoints)
        return map
    }

    private fun Int.isPowerOfTwo(): Boolean = this > 0 && this and (this - 1) == 0

    @Suppress("MagicNumber")
    private fun fourDimensionalSample(module: Module): (Int, Int) -> Double = { x: Int, y: Int ->
        //Noise range
        val x1 = 0f
        val x2 = 2f
        val y1 = 0f
        val y2 = 2f
        val dx = x2 - x1
        val dy = y2 - y1

        //Sample noise at smaller intervals
        val s = x / width.toFloat()
        val t = y / height.toFloat()

        // Calculate 4D coordinates
        val px = (x1 + cos(s * 2 * PI) * dx / (2 * PI)).toDouble()
        val py = (y1 + cos(t * 2 * PI) * dy / (2 * PI)).toDouble()
        val pz = (x1 + sin(s * 2 * PI) * dx / (2 * PI)).toDouble()
        val pw = (y1 + sin(t * 2 * PI) * dy / (2 * PI)).toDouble()

        module[px, py, pz, pw]
    }

    private fun generateSamplesWithFunction(func: (Int, Int) -> Double): FloatArray {
        val samples = FloatArray(width * height)
        for (x in 0 until width)
            for (y in 0 until height)
                samples[x * width + y] = func(x, y).toFloat()

        val min = samples.reduce { acc, v -> if (acc < v) acc else v }
        val max = samples.reduce { acc, v -> if (acc > v) acc else v }
        for (i in samples.indices) samples[i] = (samples[i] - min) / (max - min)

        return samples
    }

    private fun generateBaseLand(
        map: Array<MapLegend>,
        coordinatePoints: Array<Point>
    ) {
        val terrainModule = moduleFactory()
        val terrainFunction = fourDimensionalSample(terrainModule)
        val terrainSamples = generateSamplesWithFunction(terrainFunction)

        // Create base terrain with oceans/land/mountains
        coordinatePoints.forEach { point ->
            val sample = terrainSamples[point.sampleCoordinate]
            val mapLegendValue = when {
                sample < configuration.oceanMaxHeight -> MapLegend.OCEAN
                sample < configuration.plainMaxHeight -> MapLegend.PLAIN
                else -> MapLegend.MOUNTAIN
            }
            map[point.sampleCoordinate] = mapLegendValue
        }
    }

    private fun drawBeaches(
        map: Array<MapLegend>,
        coordinatePoints: Array<Point>
    ) {
        fun paintBeach(point: Point) {
            if (map[point.sampleCoordinate] == MapLegend.OCEAN) {
                arrayOf(
                    point.pN.sampleCoordinate to map[point.pN.sampleCoordinate],
                    point.pS.sampleCoordinate to map[point.pS.sampleCoordinate],
                    point.pE.sampleCoordinate to map[point.pE.sampleCoordinate],
                    point.pW.sampleCoordinate to map[point.pW.sampleCoordinate],
                    point.pNW.sampleCoordinate to map[point.pNW.sampleCoordinate],
                    point.pNE.sampleCoordinate to map[point.pNE.sampleCoordinate],
                    point.pSW.sampleCoordinate to map[point.pSW.sampleCoordinate],
                    point.pSE.sampleCoordinate to map[point.pSE.sampleCoordinate]
                ).forEach { (sampleCoordinate, mapLegendValue) ->
                    if (mapLegendValue == MapLegend.PLAIN)
                        map[sampleCoordinate] = MapLegend.BEACH
                }
            }
        }

        coordinatePoints.forEach { point -> paintBeach(point) }
    }

    @Suppress("MagicNumber")
    private fun drawForest(
        map: Array<MapLegend>,
        coordinatePoints: Array<Point>
    ) {
        fun mapLegendForSample(point: Point, sample: Float) = when {
            sample < 0.65f -> map[point.sampleCoordinate]
            sample < 0.7f -> MapLegend.FOREST_1
            sample < 0.75f -> MapLegend.FOREST_2
            sample < 0.8f -> MapLegend.FOREST_3
            else -> MapLegend.FOREST_4
        }

        fun paintForest(forestSample: FloatArray, point: Point) {
            if (map[point.sampleCoordinate] == MapLegend.PLAIN) {
                val sample = forestSample[point.sampleCoordinate]
                map[point.sampleCoordinate] = mapLegendForSample(point, sample)
            }
        }

        for (t in 0 until configuration.forestPasses) {
            val module = moduleFactory()
            val forestFunction = fourDimensionalSample(module)
            val forestSample = generateSamplesWithFunction(forestFunction)
            coordinatePoints.forEach { point -> paintForest(forestSample, point) }
        }
    }

    private fun drawDesert(
        map: Array<MapLegend>,
        coordinatePoints: Array<Point>
    ) {
        fun drawPoint(forestSample: FloatArray, point: Point) {
            if (map[point.sampleCoordinate] == MapLegend.PLAIN) {
                val sample = forestSample[point.sampleCoordinate]
                map[point.sampleCoordinate] = when {
                    sample < configuration.desertMinimumHeight -> map[point.sampleCoordinate]
                    else -> MapLegend.DESERT
                }
            }
        }

        for (t in 0 until configuration.desertPasses) {
            val module = moduleFactory()
            val forestFunction = fourDimensionalSample(module)
            val forestSample = generateSamplesWithFunction(forestFunction)
            coordinatePoints.forEach { point -> drawPoint(forestSample, point) }
        }
    }

    private fun point(x: Int, y: Int) = Point(width, height, x, y)
}
