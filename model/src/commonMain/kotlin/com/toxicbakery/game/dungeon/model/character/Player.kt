@file:Suppress("MagicNumber")
package com.toxicbakery.game.dungeon.model.character

import com.toxicbakery.game.dungeon.model.character.stats.Stats
import kotlinx.serialization.protobuf.ProtoNumber
import kotlinx.serialization.Serializable

@Serializable
data class Player(
    @ProtoNumber(1)
    override val id: Int = 0,
    @ProtoNumber(2)
    override val name: String = "",
    @ProtoNumber(3)
    override val stats: Stats = Stats(),
    @ProtoNumber(4)
    override val statsBase: Stats = Stats(),
    @ProtoNumber(5)
    val global: Global = Global(),
    @ProtoNumber(6)
    override val location: Location = Location()
) : Character {

    /**
     * Representation of the [statsBase] + [stats]. As
     */
    fun getComputedStats(): Stats = statsBase + stats

}
