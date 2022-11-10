package com.toxicbakery.game.dungeon.model.character

import com.toxicbakery.game.dungeon.model.Displayable
import com.toxicbakery.game.dungeon.model.Identifiable
import com.toxicbakery.game.dungeon.model.Named
import com.toxicbakery.game.dungeon.model.character.equipment.Equipable
import com.toxicbakery.game.dungeon.model.character.equipment.EquipmentSlot
import com.toxicbakery.game.dungeon.model.character.stats.Stats

interface Character : Identifiable, Displayable, Named {

    /**
     * Displayable name of the character.
     */
    override val name: String

    /**
     * Determine if a character can be attacked and killed.
     */
    val canBeKilled: Boolean
        get() = true

    /**
     * Stats of a player for interactions with the world. These can be modified by events such as fighting or
     * consuming items.
     */
    val stats: Stats

    /**
     * Base stats of the character.
     */
    val statsBase: Stats

    /**
     * Equipment worn or held by the character.
     */
    val equipmentSlotMap: Map<EquipmentSlot, Equipable>
        get() = mapOf()
}
