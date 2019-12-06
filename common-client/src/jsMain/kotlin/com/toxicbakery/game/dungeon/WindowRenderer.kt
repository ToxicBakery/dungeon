package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.map.MapLegend
import com.toxicbakery.game.dungeon.map.model.Window
import kotlinx.html.br
import kotlinx.html.stream.appendHTML

actual class WindowRenderer(
    private val printer: (String) -> Unit
) {

    private fun Appendable.htmlBr() = appendHTML(false).br()

    actual fun render(window: Window) = window
        .windowRows
        .joinToString(buildString { htmlBr() }) { windowRow ->
            windowRow.row.joinToString("") { byte -> byte.toTile() }
        }
        .let { output -> printer(output) }

    private fun Byte.toTile(): String = MapLegend.lookupMap
        .getOrElse(this, { MapLegend.NULL })
        .toTile()
    
    private fun MapLegend.toTile():String = when (this) {
        MapLegend.NULL -> "...."
        MapLegend.PLAYER -> "&lt;oo&gt;"
        MapLegend.NPC -> "&lt;..&gt;"
        MapLegend.FOREST_1 -> ".^.."
        MapLegend.FOREST_2 -> ".^.^"
        MapLegend.FOREST_3 -> ".^^^"
        MapLegend.FOREST_4 -> "^^^^"
        MapLegend.OCEAN -> "~~~~"
        MapLegend.RIVER -> "~~~~"
        MapLegend.DESERT -> "...."
        MapLegend.PLAIN -> "...."
        MapLegend.BEACH_N -> "...."
        MapLegend.BEACH_S -> "...."
        MapLegend.BEACH_W -> "~..."
        MapLegend.BEACH_E -> "...~"
        MapLegend.ANIMAL_AGGRESSIVE -> ".&gt;&lt;."
        MapLegend.ANIMAL_PASSIVE -> ".&lt;&gt;."
        MapLegend.CREATURE -> "&gt;&amp;&amp;&lt;"
    }

}
