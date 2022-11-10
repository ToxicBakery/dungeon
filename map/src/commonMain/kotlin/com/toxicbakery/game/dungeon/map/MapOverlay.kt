package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.model.world.Location

/**
 * Represents the ability to draw items onto the map with world positions at render time.
 */
interface MapOverlay {
    /**
     * Render an item onto the screen post render of the map.
     */
    fun addOverlayItem(
        location: Location,
        mapLegend: MapLegend,
    )
}
