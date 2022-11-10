package com.toxicbakery.game.dungeon.model.world

import com.toxicbakery.game.dungeon.model.Lookable
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class LookLocation(
    @ProtoNumber(1)
    val location: Location,
    @ProtoNumber(2)
    val lookables: List<Lookable>,
    @ProtoNumber(3)
    val mapLegendByte: Byte,
    @ProtoNumber(4)
    val world: World,
)
