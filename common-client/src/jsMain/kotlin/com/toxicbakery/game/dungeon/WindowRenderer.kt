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
            windowRow.joinToString("", transform = { byte ->
                MapLegend.representingByte(byte).htmlRepresentation
            })
        }
        .let { output -> printer(output) }
}
