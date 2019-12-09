package com.toxicbakery.game.dungeon.map

internal class Point(
    private val width: Int,
    private val height: Int,
    val x: Int,
    val y: Int
) {

    val sampleCoordinate: Int = x * width + y

    val pN: Point
        get() = newPoint(x, wrapY(y - 1))

    val pS: Point
        get() = newPoint(x, wrapY(y + 1))

    val pE: Point
        get() = newPoint(wrapX(x + 1), y)

    val pW: Point
        get() = newPoint(wrapX(x - 1), y)

    val pNW: Point
        get() = newPoint(wrapX(x - 1), wrapY(y - 1))

    val pNE: Point
        get() = newPoint(wrapX(x + 1), wrapY(y - 1))

    val pSW: Point
        get() = newPoint(wrapX(x - 1), wrapY(y + 1))

    val pSE: Point
        get() = newPoint(wrapX(x + 1), wrapY(y + 1))

    private fun wrapY(nY: Int) = when {
        nY < 0 -> height - 1
        nY == height -> 0
        else -> nY
    }

    private fun wrapX(nX: Int) = when {
        nX < 0 -> width - 1
        nX == width -> 0
        else -> nX
    }

    private fun newPoint(x: Int, y: Int) = Point(width, height, x, y)

}
