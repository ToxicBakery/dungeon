package com.toxicbakery.game.dungeon.character

import com.toxicbakery.game.dungeon.Identifiable
import com.toxicbakery.game.dungeon.character.equipment.Equipable
import com.toxicbakery.game.dungeon.character.equipment.EquipmentSlot
import com.toxicbakery.game.dungeon.character.stats.Stats

interface Character : Identifiable {

    /**
     * Displayable name of the character.
     */
    val name: String

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
