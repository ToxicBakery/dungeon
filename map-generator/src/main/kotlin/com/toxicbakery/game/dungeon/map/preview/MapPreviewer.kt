package com.toxicbakery.game.dungeon.map.preview

import com.toxicbakery.game.dungeon.map.MapLegend

interface MapPreviewer {

    fun preview(
        mapSize:
        Int, mapData: Array<MapLegend>
    )

}
