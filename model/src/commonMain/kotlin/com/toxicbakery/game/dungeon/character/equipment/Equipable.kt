package com.toxicbakery.game.dungeon.character.equipment

interface Equipable {
    val name: String
    val slot: EquipmentSlot
    val equipmentType: EquipmentType
}
