package com.toxicbakery.game.dungeon.model.character

import com.toxicbakery.game.dungeon.model.Displayable
import com.toxicbakery.game.dungeon.model.Identifiable
import com.toxicbakery.game.dungeon.model.Living
import com.toxicbakery.game.dungeon.model.Locatable
import com.toxicbakery.game.dungeon.model.Named
import com.toxicbakery.game.dungeon.model.character.equipment.Equipable
import com.toxicbakery.game.dungeon.model.character.equipment.EquipmentSlot

interface Character : Identifiable, Displayable, Named, Locatable, Living {

    /**
     * Displayable name of the character.
     */
    override val name: String

    /**
     * Equipment worn or held by the character.
     */
    val equipmentSlotMap: Map<EquipmentSlot, Equipable>
        get() = mapOf()
}
