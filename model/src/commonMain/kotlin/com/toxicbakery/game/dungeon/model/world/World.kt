package com.toxicbakery.game.dungeon.model.world

import com.toxicbakery.game.dungeon.model.Identifiable
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class World(
    @ProtoNumber(1)
    override val id: Int,
    @ProtoNumber(2)
    val name: String
) : Identifiable
