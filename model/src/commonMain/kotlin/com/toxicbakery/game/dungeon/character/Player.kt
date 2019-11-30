@file:Suppress("MagicNumber")
package com.toxicbakery.game.dungeon.character

import com.toxicbakery.game.dungeon.character.stats.Stats
import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable

@Serializable
data class Player(
    @SerialId(1)
    override val id: Int = 0,
    @SerialId(2)
    override val name: String = "",
    @SerialId(3)
    override val stats: Stats = Stats(),
    @SerialId(4)
    override val statsBase: Stats = Stats(),
    @SerialId(5)
    val global: Global = Global(),
    @SerialId(6)
    val location: Location = Location()
) : Character
