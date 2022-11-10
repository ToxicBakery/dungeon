package com.toxicbakery.game.dungeon.map.preview

import com.toxicbakery.game.dungeon.map.MapData

interface MapPreviewer {

    fun preview(
        mapSize: Int,
        mapData: MapData
    )
}
