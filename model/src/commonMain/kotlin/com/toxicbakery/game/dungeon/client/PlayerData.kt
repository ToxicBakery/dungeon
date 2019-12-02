@file:Suppress("MagicNumber")

package com.toxicbakery.game.dungeon.client

import com.toxicbakery.game.dungeon.character.Location
import com.toxicbakery.game.dungeon.character.stats.Stats
import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    @SerialId(1)
    val worldName: String = "",
    @SerialId(2)
    val location: Location = Location(),
    @SerialId(3)
    val maxHealth: Int = 0,
    @SerialId(4)
    val stats: Stats = Stats()
)
