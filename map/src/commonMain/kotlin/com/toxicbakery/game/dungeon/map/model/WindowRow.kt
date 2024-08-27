package com.toxicbakery.game.dungeon.map.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class WindowRow(
    @ProtoNumber(1)
    val row: List<Byte>
)

data class WindowRowMutable(
    val row: MutableList<Byte>
) {

    fun toWindowRow(): WindowRow = WindowRow(
        row = row.toList()
    )
}
