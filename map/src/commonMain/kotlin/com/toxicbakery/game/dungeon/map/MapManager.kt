package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.map.model.Window
import org.kodein.di.DI

interface MapManager {

    /**
     * Size of the map on the x or y axis
     */
    fun mapSize(): Int

    /**
     * Draw a window returning a list of lists of bytes to be rendered by the client. Returned value is a perfectly
     * squared return such that row count equals column count.
     */
    fun drawWindow(
        windowDescription: WindowDescription,
        mapOverlay: (MapOverlay) -> Unit,
    ): Window

    /**
     * Get the tile representing a single location.
     */
    fun drawLocation(
        windowDescription: WindowDescription
    ): Byte

    /**
     * Return the map as a Window representing the complete map. This is memory expensive as the entire map must be
     * loaded to memory to create the window and as such should only be used for debug purposes.
     */
    fun drawCompleteMap(): Window
}

expect val mapManagerModule: DI.Module
