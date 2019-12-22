package com.toxicbakery.game.dungeon.model.creature

import com.toxicbakery.game.dungeon.model.Displayable
import com.toxicbakery.game.dungeon.model.Identifiable
import com.toxicbakery.game.dungeon.model.Named
import com.toxicbakery.game.dungeon.model.character.Location
import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable

@Serializable
data class Creature(
    @SerialId(1)
    override val id: Int = 0,
    @SerialId(2)
    override val name: String,
    @SerialId(3)
    override val location: Location
) : Identifiable, Displayable, Named
