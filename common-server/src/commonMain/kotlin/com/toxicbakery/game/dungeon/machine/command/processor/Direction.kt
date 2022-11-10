package com.toxicbakery.game.dungeon.machine.command.processor

import com.toxicbakery.game.dungeon.map.model.Direction

object Direction {
    internal const val DIR_N = "n"
    internal const val DIR_S = "s"
    internal const val DIR_W = "w"
    internal const val DIR_E = "e"
    internal const val DIR_N_LONG = "north"
    internal const val DIR_S_LONG = "south"
    internal const val DIR_W_LONG = "west"
    internal const val DIR_E_LONG = "east"

    internal val directionMap: Map<String, Direction> = mapOf(
        DIR_N to Direction.NORTH,
        DIR_N_LONG to Direction.NORTH,
        DIR_S to Direction.SOUTH,
        DIR_S_LONG to Direction.SOUTH,
        DIR_W to Direction.WEST,
        DIR_W_LONG to Direction.WEST,
        DIR_E to Direction.EAST,
        DIR_E_LONG to Direction.EAST
    )
}
