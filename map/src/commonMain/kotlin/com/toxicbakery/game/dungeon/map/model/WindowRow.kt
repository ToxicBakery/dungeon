package com.toxicbakery.game.dungeon.map.model

import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable

@Serializable
data class WindowRow(
    @SerialId(1)
    val row: List<Byte>
)

data class WindowRowMutable(
    val row: MutableList<Byte>
) {

    fun toWindowRow(): WindowRow = WindowRow(
        row = row.toList()
    )

}
