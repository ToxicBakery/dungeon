package com.toxicbakery.game.dungeon.model.creature

import com.toxicbakery.game.dungeon.model.Displayable
import com.toxicbakery.game.dungeon.model.Identifiable
import com.toxicbakery.game.dungeon.model.Named
import com.toxicbakery.game.dungeon.model.character.Location
import kotlinx.serialization.protobuf.ProtoNumber
import kotlinx.serialization.Serializable

@Serializable
data class Creature(
    @ProtoNumber(1)
    override val id: Int = 0,
    @ProtoNumber(2)
    override val name: String,
    @ProtoNumber(3)
    override val location: Location
) : Identifiable, Displayable, Named
