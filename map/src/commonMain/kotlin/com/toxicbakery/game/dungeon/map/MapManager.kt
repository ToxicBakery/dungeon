package com.toxicbakery.game.dungeon.map

import org.kodein.di.Kodein

interface MapManager {

    /**
     * Draw a window returning a list of lists of bytes to be rendered by the client. Returned value is a perfectly
     * squared return such that row count equals column count.
     */
    fun drawWindow(window: Window): List<List<Byte>>

}

expect val mapManagerModule: Kodein.Module
