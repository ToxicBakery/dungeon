@file:Suppress("MagicNumber")

package com.toxicbakery.game.dungeon.model.character.stats

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Stats(
    @ProtoNumber(1)
    val health: Int = 0,
    @ProtoNumber(2)
    val strength: Int = 0,
    @ProtoNumber(3)
    val dexterity: Int = 0,
    @ProtoNumber(4)
    val defence: Int = 0,
    @ProtoNumber(5)
    val luck: Int = 0,
    @ProtoNumber(6)
    val stamina: Int = 0,
) {

    operator fun plus(other: Stats): Stats =
        Stats(
            health = other.health, // Health is subtracted so only the remaining health needs to be shown
            strength = strength + other.strength,
            dexterity = dexterity + other.dexterity,
            defence = defence + other.defence,
            luck = luck + other.luck,
            stamina = stamina + other.stamina,
        )
}
