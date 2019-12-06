package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.map.MapLegend.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.random.Random

@Suppress("MagicNumber", "ForEachParameterNotUsed")
class TerrainGenerator(
    val config: Config,
    val random: Random = Random.Default
) {

    private val sampleGenerator = SampleGenerator(config, random)

    fun generate(): List<List<ByteArray>> {
        do {
            val map: ByteArray = sample()
            val count = IntArray(255) { 0 }
            (0 until config.size * config.size).forEach { i -> count[map[i].toInt()] += 1 }

            if (count[MOUNTAIN.byteRepresentation.toInt()] < 100) continue
            if (count[PLAIN.byteRepresentation.toInt()] < 100) continue
            if (count[DESERT.byteRepresentation.toInt()] < 100) continue
            if (count[FOREST_4.byteRepresentation.toInt()] < 100) continue

            return (0 until config.size / config.regionSize).map { ry ->
                (0 until config.size / config.regionSize).map { rx ->
                    val region = ByteArray(config.regionSize * config.regionSize)
                    (0 until config.regionSize).forEach { cy ->
                        (0 until config.regionSize).forEach { cx ->
                            region[cy * config.size + cx] = map[ry * config.size + rx * config.regionSize]
                        }
                    }
                    region
                }
            }

        } while (true)
    }

    private fun sample(): ByteArray {
        val noiseS1 = sampleGenerator.sample(SAMPLE_SMALL)
        val noiseS2 = sampleGenerator.sample(SAMPLE_SMALL)
        val noiseS3 = sampleGenerator.sample(SAMPLE_SMALL)
        val noiseB1 = sampleGenerator.sample(SAMPLE_LARGE)
        val noiseB2 = sampleGenerator.sample(SAMPLE_LARGE)

        val map = ByteArray(config.size * config.size) { 0.toByte() }
        (0 until config.size).forEach { y ->
            (0 until config.size).forEach { x ->
                val i = x + y * config.size
                var v1 = abs(noiseB1[i] - noiseB2[i]) * 3 - 2
                val v2 = abs(abs(noiseS1[i] - noiseS2[i]) - noiseS3[i]) * 3 - 2
                val d = (config.size / (config.size - 1.0) * 2 - 1).let { if (it < 0) -it else it }
                val dist = d.pow(7)
                v1 = v1 + 1 - dist * 20
                map[i] = when {
                    v1 < config.noiseWater -> OCEAN.byteRepresentation
                    v1 > config.noiseMountain && v2 < config.noiseGround -> MOUNTAIN.byteRepresentation
                    else -> PLAIN.byteRepresentation
                }
            }
        }

        for (i in (0 until config.size * config.size / config.ratioTree)) {
            val xs = random.nextInt(config.size)
            val ys = random.nextInt(config.size)
            (0 until 10).forEach {
                val x = xs + random.nextInt(21) - 10
                val y = ys + random.nextInt(21) - 10
                (0 until 100).forEach {
                    val xo = x + random.nextInt(5) - random.nextInt(5)
                    val yo = y + random.nextInt(5) - random.nextInt(5)
                    (yo - 1..yo + 1).forEach { yy ->
                        (xo - 1..xo + 1).forEach { xx ->
                            if (xx >= 0 && yy >= 0 && xx < config.size && yy < config.size) {
                                if (map[xx + yy * config.size] == PLAIN.byteRepresentation)
                                    map[xx + yy * config.size] == DESERT.byteRepresentation
                            }
                        }
                    }
                }
            }
        }

        for (i in (0 until config.size * config.size / config.ratioTree)) {
            val x = random.nextInt(config.size)
            val y = random.nextInt(config.size)
            for (j in (0 until 200)) {
                val xx = x + random.nextInt(5) - random.nextInt(5)
                val yy = x + random.nextInt(5) - random.nextInt(5)
                if (xx >= 0 && yy >= 0 && xx < config.size && yy < config.size) {
                    if (map[xx + yy * config.size] == PLAIN.byteRepresentation)
                        map[xx + yy * config.size] = FOREST_4.byteRepresentation
                }
            }
        }

        return map
    }

    private fun Int.isPowerOfTwo(): Boolean = this > 0 && this and (this - 1) == 0

    companion object {
        private const val SAMPLE_SMALL = 16
        private const val SAMPLE_LARGE = 32
    }

}
