package com.toxicbakery.game.dungeon.map.model

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
}
