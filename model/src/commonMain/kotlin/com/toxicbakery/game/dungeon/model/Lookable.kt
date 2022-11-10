package com.toxicbakery.game.dungeon.model

import com.toxicbakery.game.dungeon.model.character.Character
import com.toxicbakery.game.dungeon.model.character.Global
import com.toxicbakery.game.dungeon.model.character.stats.Stats
import com.toxicbakery.game.dungeon.model.world.Location
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Suppress("SpellCheckingInspection")
@OptIn(ExperimentalSerializationApi::class)
@Serializable
sealed class Lookable : Displayable, Named {

    @Serializable
    data class Animal(
        @ProtoNumber(1)
        override val id: Int = 0,
        @ProtoNumber(2)
        override val name: String,
        @ProtoNumber(3)
        override val location: Location,
        @ProtoNumber(4)
        val isPassive: Boolean
    ) : Lookable(), Identifiable

    @Serializable
    data class Creature(
        @ProtoNumber(1)
        override val id: Int = 0,
        @ProtoNumber(2)
        override val name: String,
        @ProtoNumber(3)
        override val location: Location,
        @ProtoNumber(4)
        val isPassive: Boolean,
    ) : Lookable(), Identifiable

    @Serializable
    data class Npc(
        @ProtoNumber(1)
        override val id: Int,
        @ProtoNumber(2)
        override val location: Location,
        @ProtoNumber(3)
        override val name: String,
        @ProtoNumber(4)
        override val stats: Stats,
        @ProtoNumber(5)
        override val statsBase: Stats,
        @ProtoNumber(6)
        override val canBeKilled: Boolean = false,
    ) : Lookable(), Character

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
    ) : Lookable(), Character {

        /**
         * Representation of the [statsBase] + [stats]. As
         */
        fun getComputedStats(): Stats = statsBase + stats
    }
}
