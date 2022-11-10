package com.toxicbakery.game.dungeon.model.world

import kotlin.math.abs

@Suppress("NOTHING_TO_INLINE")
data class Distance(
    val x: Int,
    val y: Int,
) {
    companion object {
        inline fun wrappedDistance(
            x1: Int,
            x2: Int,
            y1: Int,
            y2: Int,
            wrap: Int
        ) = Distance(
            x = wrappedDistanceLine(x1, x2, wrap),
            y = wrappedDistanceLine(y1, y2, wrap),
        )

        inline fun wrappedDistanceLine(
            a: Int,
            b: Int,
            wrap: Int
        ): Int = abs(a - b).let { r ->
            if (r > wrap / 2) wrap - r
            else a - b
        }
    }
}
