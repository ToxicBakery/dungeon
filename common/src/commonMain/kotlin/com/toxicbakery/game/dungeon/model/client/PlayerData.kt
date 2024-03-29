@file:Suppress("MagicNumber")

package com.toxicbakery.game.dungeon.model.client

import com.toxicbakery.game.dungeon.model.character.stats.Stats
import com.toxicbakery.game.dungeon.model.world.Location
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class PlayerData(
    @ProtoNumber(1)
    val worldName: String = "",
    @ProtoNumber(2)
    val location: Location = Location(),
    @ProtoNumber(3)
    val maxHealth: Int = 0,
    @ProtoNumber(4)
    val stats: Stats = Stats()
)
