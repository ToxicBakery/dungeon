@file:Suppress("MagicNumber")
package com.toxicbakery.game.dungeon.model.character.stats

import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable

@Serializable
data class Stats(
    @SerialId(1)
    val health: Int = 0,
    @SerialId(2)
    val strength: Int = 0,
    @SerialId(3)
    val dexterity: Int = 0,
    @SerialId(4)
    val defence: Int = 0,
    @SerialId(5)
    val luck: Int = 0
) {

    operator fun plus(other: Stats): Stats =
        Stats(
            health = other.health, // Health is subtracted so only the remaining health needs to be shown
            strength = strength + other.strength,
            dexterity = dexterity + other.dexterity,
            defence = defence + other.defence,
            luck = luck + other.luck
        )

}
