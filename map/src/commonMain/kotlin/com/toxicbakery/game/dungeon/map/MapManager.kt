package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.map.model.WindowMutable
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
    fun drawWindow(windowDescription: WindowDescription): WindowMutable

}

expect val mapManagerModule: Kodein.Module
