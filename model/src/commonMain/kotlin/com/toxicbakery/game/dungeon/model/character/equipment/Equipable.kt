package com.toxicbakery.game.dungeon.model.character.equipment

interface Equipable {
    val name: String
    val slot: EquipmentSlot
    val equipmentType: EquipmentType
    val weight: Int
}
