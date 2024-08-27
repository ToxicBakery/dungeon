package com.toxicbakery.game.dungeon.map.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Window(
    @ProtoNumber(1)
    val windowRows: List<ByteArray>
)
