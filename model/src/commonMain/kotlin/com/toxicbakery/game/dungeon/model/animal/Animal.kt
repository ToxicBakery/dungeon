package com.toxicbakery.game.dungeon.model.animal

import com.toxicbakery.game.dungeon.model.Displayable
import com.toxicbakery.game.dungeon.model.Identifiable
import com.toxicbakery.game.dungeon.model.Named
import com.toxicbakery.game.dungeon.model.character.Location
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/**
 * Animals roam freely in the world and can be harvested for materials and nutrients.
 *
 * @param passive indicates if the animal is docile or aggressive
 */
@Serializable
data class Animal(
    @ProtoNumber(1)
    override val id: Int = 0,
    @ProtoNumber(2)
    override val name: String,
    @ProtoNumber(3)
    override val location: Location,
    @ProtoNumber(4)
    val passive: Boolean
) : Identifiable, Displayable, Named
