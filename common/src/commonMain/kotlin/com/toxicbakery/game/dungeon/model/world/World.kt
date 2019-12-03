package com.toxicbakery.game.dungeon.model.world

import com.toxicbakery.game.dungeon.model.Identifiable
import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable

@Serializable
data class World(
    @SerialId(1)
    override val id: Int,
    @SerialId(2)
    val name: String
) : Identifiable
