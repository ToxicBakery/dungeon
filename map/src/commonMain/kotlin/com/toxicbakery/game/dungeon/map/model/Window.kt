package com.toxicbakery.game.dungeon.map.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class Window(
    @ProtoNumber(1)
    val windowRows: List<ByteArray>
)
