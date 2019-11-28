package com.toxicbakery.game.dungeon.character

import com.toxicbakery.game.dungeon.Identifiable
import com.toxicbakery.game.dungeon.character.equipment.Equipable
import com.toxicbakery.game.dungeon.character.equipment.EquipmentSlot
import com.toxicbakery.game.dungeon.character.type.CharacterType

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

    val characterType: CharacterType

    /**
     * Equipment worn or held by the character.
     */
    val equipmentSlotMap: Map<EquipmentSlot, Equipable>
        get() = mapOf()

}
