package com.toxicbakery.game.dungeon.ui

import com.toxicbakery.game.dungeon.map.MapLegend

internal fun MapLegend.locationDescription() = when (this) {
    MapLegend.FOREST_1,
    MapLegend.FOREST_2 -> "A few trees here"

    MapLegend.FOREST_3,
    MapLegend.FOREST_4 -> "You peer through the thick woods"

    MapLegend.OCEAN -> "The dark ocean peers back at you"
    MapLegend.RIVER -> "A raging river runs here"
    MapLegend.DESERT -> "An uncomfortable desert"
    MapLegend.PLAIN -> "Fields of grass"
    MapLegend.BEACH -> "You hear the waves crashing against the beach"
    MapLegend.LAKE -> "A lake, maybe it has fish"
    MapLegend.MOUNTAIN -> "Mountains, difficult and dangerous"
    else -> "You have no idea what you're looking at..."
}
