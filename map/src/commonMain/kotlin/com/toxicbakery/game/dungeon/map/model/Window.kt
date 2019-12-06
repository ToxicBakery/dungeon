package com.toxicbakery.game.dungeon.map.model

import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable

@Serializable
data class Window(
    @SerialId(1)
    val windowRows: List<WindowRow>
)

data class WindowMutable(
    val windowRows: MutableList<WindowRowMutable>
) {

    fun toWindow():Window = Window(
        windowRows = windowRows.map(WindowRowMutable::toWindowRow)
    )

}
