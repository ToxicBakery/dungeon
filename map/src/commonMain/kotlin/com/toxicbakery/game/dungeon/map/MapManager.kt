package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.map.model.Window
import org.kodein.di.Kodein

interface MapManager {

    /**
     * Size of the map on the x or y axis
     */
    fun mapSize(): Int

    /**
     * Draw a window returning a list of lists of bytes to be rendered by the client. Returned value is a perfectly
     * squared return such that row count equals column count.
     */
    fun drawWindow(windowDescription: WindowDescription): Window

    /**
     * Return the map as a Window representing the complete map. This is memory expensive as the entire map must be
     * loaded to memory to create the window.
     */
    fun drawCompleteMap(): Window

}

expect val mapManagerModule: Kodein.Module
