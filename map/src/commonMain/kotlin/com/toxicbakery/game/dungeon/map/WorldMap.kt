package com.toxicbakery.game.dungeon.map

import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable

@Serializable
class WorldMap(
    @SerialId(1)
    val data: List<List<Byte>>
)
