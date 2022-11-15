package com.toxicbakery.game.dungeon.map.model

import kotlin.jvm.JvmStatic
import kotlin.random.Random

enum class Direction() {
    NORTH,
    SOUTH,
    WEST,
    EAST;

    /**
     * Return the opposite direction useful for describing where something came from.
     */
    val sourceDirection: Direction by lazy {
        when (this) {
            NORTH -> SOUTH
            SOUTH -> NORTH
            WEST -> EAST
            EAST -> WEST
        }
    }

    companion object {
        private val values = Direction.values()

        @JvmStatic
        fun getRandomDirection(): Direction = values[Random.nextInt(values.size)]
    }
}
