package com.toxicbakery.game.dungeon.map

import kotlin.random.Random

class SampleGenerator(
    private val config: Config,
    private val random: Random
) {

    private val wrapSize = config.size - 1

    fun sample(stepSize: Int): DoubleArray {
        val values = DoubleArray(config.size * config.size) { performRandom() }
        var scale = 1.0 / config.size
        var scaleModifier = 1.0
        var _stepSize = stepSize

        do {
            val halfStep = _stepSize / 2
            (0 until config.size step _stepSize).forEach { y ->
                (0 until config.size step _stepSize).forEach { x ->
                    val a = values.get(x, y)
                    val b = values.get(x + _stepSize, y)
                    val c = values.get(x, y + _stepSize)
                    val d = values.get(x + _stepSize, y + _stepSize)
                    val e = (a + b + c + d) / 4.0 + performRandom() * _stepSize * scale
                    values.set(x + halfStep, y + halfStep, e)
                }
            }
            (0 until config.size step _stepSize).forEach { y ->
                (0 until config.size step _stepSize).forEach { x ->
                    val a = values.get(x, y)
                    val b = values.get(x + _stepSize, y)
                    val c = values.get(x, y + _stepSize)
                    val d = values.get(x + halfStep, y + halfStep)
                    val e = values.get(x + halfStep, y - halfStep)
                    val f = values.get(x - halfStep, y + halfStep)
                    val g = (a + b + d + e) / 4.0 + performRandom() * _stepSize * scale * 0.5
                    val h = (a + c + d + f) / 4.0 + performRandom() * _stepSize * scale * 0.5
                    values.set(x + halfStep, y, g)
                    values.set(x, y + halfStep, h)
                }
            }
            _stepSize /= 2
            scale *= (scaleModifier + 0.8)
            scaleModifier *= 0.3
        } while (_stepSize > 1)

        return values
    }

    private fun performRandom(): Double = random.nextFloat() * 2.0 - 1.0

    private fun DoubleArray.get(x: Int, y: Int) = get(wrappedCoord(x, y))

    private fun DoubleArray.set(x: Int, y: Int, value: Double) = set(wrappedCoord(x, y), value)

    private fun wrappedCoord(x: Int, y: Int) = x.wrappedCoord + y.wrappedCoord * config.size

    private val Int.wrappedCoord: Int
        get() = this and wrapSize

}
