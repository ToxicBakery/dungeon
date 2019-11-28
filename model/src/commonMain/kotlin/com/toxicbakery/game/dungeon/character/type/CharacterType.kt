@file:Suppress("MagicNumber")
package com.toxicbakery.game.dungeon.character.type

import kotlinx.serialization.Serializable

/**
 * Character types represent base stats of a character.
 */
@Suppress("UnnecessaryAbstractClass")
@Serializable
abstract class CharacterType {
    abstract val type: String
    abstract val health: Int
    abstract val strength: Int
    abstract val dexterity: Int
    abstract val defence: Int
    abstract val luck: Int
}

object CharacterTypeNull : CharacterType() {
    override val type: String = "null"
    override val health: Int = 0
    override val strength: Int = 0
    override val dexterity: Int = 0
    override val defence: Int = 0
    override val luck: Int = 0
}

object CharacterTypeWizard : CharacterType() {
    override val type: String = "Wizard"
    override val health: Int = 1452
    override val strength: Int = 83
    override val dexterity: Int = 415
    override val defence: Int = 15
    override val luck: Int = 5
}

object CharacterTypeBarbarian : CharacterType() {
    override val type: String = "Barbarian"
    override val health: Int = 2905
    override val strength: Int = 249
    override val dexterity: Int = 290
    override val defence: Int = 50
    override val luck: Int = 10
}

object CharacterTypeArcher : CharacterType() {
    override val type: String = "Archer"
    override val health: Int = 1245
    override val strength: Int = 83
    override val dexterity: Int = 415
    override val defence: Int = 20
    override val luck: Int = 15
}

object CharacterTypeMonk : CharacterType() {
    override val type: String = "Monk"
    override val health: Int = 1348
    override val strength: Int = 166
    override val dexterity: Int = 332
    override val defence: Int = 20
    override val luck: Int = 30
}

object CharacterTypeMountainDwarf : CharacterType() {
    override val type: String = "Mountain Dwarf"
    override val health: Int = 2075
    override val strength: Int = 207
    override val dexterity: Int = 228
    override val defence: Int = 40
    override val luck: Int = 2
}

object CharacterTypeKnight : CharacterType() {
    override val type: String = "Knight"
    override val health: Int = 3942
    override val strength: Int = 332
    override val dexterity: Int = 622
    override val defence: Int = 100
    override val luck: Int = 15
}

object CharacterTypeAvatar : CharacterType() {
    override val type: String = "Avatar"
    override val health: Int = 12450
    override val strength: Int = 622
    override val dexterity: Int = 747
    override val defence: Int = 120
    override val luck: Int = 20
}

object CharacterTypeTunneller : CharacterType() {
    override val type: String = "Tunneller"
    override val health: Int = 1452
    override val strength: Int = 166
    override val dexterity: Int = 166
    override val defence: Int = 30
    override val luck: Int = 0
}

object CharacterTypePriestess : CharacterType() {
    override val type: String = "Priestess"
    override val health: Int = 1245
    override val strength: Int = 83
    override val dexterity: Int = 332
    override val defence: Int = 20
    override val luck: Int = 6
}

object CharacterTypeGiant : CharacterType() {
    override val type: String = "Giant"
    override val health: Int = 2697
    override val strength: Int = 415
    override val dexterity: Int = 249
    override val defence: Int = 60
    override val luck: Int = 20
}

object CharacterTypeFairy : CharacterType() {
    override val type: String = "Fairy"
    override val health: Int = 622
    override val strength: Int = 41
    override val dexterity: Int = 290
    override val defence: Int = 10
    override val luck: Int = 40
}

object CharacterTypeThief : CharacterType() {
    override val type: String = "Thief"
    override val health: Int = 1037
    override val strength: Int = 124
    override val dexterity: Int = 498
    override val defence: Int = 20
    override val luck: Int = 14
}

object CharacterTypeSamurai : CharacterType() {
    override val type: String = "Samurai"
    override val health: Int = 2905
    override val strength: Int = 332
    override val dexterity: Int = 373
    override val defence: Int = 60
    override val luck: Int = 20
}

object CharacterTypeHornedReaper : CharacterType() {
    override val type: String = "Horned Reaper"
    override val health: Int = 8300
    override val strength: Int = 622
    override val dexterity: Int = 664
    override val defence: Int = 70
    override val luck: Int = 30
}

object CharacterTypeSkeleton : CharacterType() {
    override val type: String = "Skeleton"
    override val health: Int = 2075
    override val strength: Int = 228
    override val dexterity: Int = 290
    override val defence: Int = 20
    override val luck: Int = 2
}

object CharacterTypeTroll : CharacterType() {
    override val type: String = "Troll"
    override val health: Int = 1867
    override val strength: Int = 166
    override val dexterity: Int = 207
    override val defence: Int = 35
    override val luck: Int = 10
}

object CharacterTypeDragon : CharacterType() {
    override val type: String = "Dragon"
    override val health: Int = 3735
    override val strength: Int = 373
    override val dexterity: Int = 249
    override val defence: Int = 90
    override val luck: Int = 18
}

object CharacterTypeDemonSpawn : CharacterType() {
    override val type: String = "Demon Spawn"
    override val health: Int = 1348
    override val strength: Int = 207
    override val dexterity: Int = 290
    override val defence: Int = 40
    override val luck: Int = 8
}

object CharacterTypeFly : CharacterType() {
    override val type: String = "Fly"
    override val health: Int = 622
    override val strength: Int = 41
    override val dexterity: Int = 207
    override val defence: Int = 10
    override val luck: Int = 15
}

object CharacterTypeMistress : CharacterType() {
    override val type: String = "Mistress"
    override val health: Int = 2905
    override val strength: Int = 249
    override val dexterity: Int = 290
    override val defence: Int = 50
    override val luck: Int = 20
}

object CharacterTypeWarlock : CharacterType() {
    override val type: String = "Warlock"
    override val health: Int = 1452
    override val strength: Int = 83
    override val dexterity: Int = 415
    override val defence: Int = 15
    override val luck: Int = 6
}

object CharacterTypeBileDemon : CharacterType() {
    override val type: String = "Bile Demon"
    override val health: Int = 4980
    override val strength: Int = 332
    override val dexterity: Int = 166
    override val defence: Int = 60
    override val luck: Int = 5
}

object CharacterTypeImp : CharacterType() {
    override val type: String = "Imp"
    override val health: Int = 311
    override val strength: Int = 20
    override val dexterity: Int = 249
    override val defence: Int = 5
    override val luck: Int = 0
}

object CharacterTypeBeetle : CharacterType() {
    override val type: String = "Beetle"
    override val health: Int = 1037
    override val strength: Int = 103
    override val dexterity: Int = 228
    override val defence: Int = 25
    override val luck: Int = 3
}

object CharacterTypeVampire : CharacterType() {
    override val type: String = "Vampire"
    override val health: Int = 3320
    override val strength: Int = 290
    override val dexterity: Int = 332
    override val defence: Int = 30
    override val luck: Int = 25
}

object CharacterTypeSpider : CharacterType() {
    override val type: String = "Spider"
    override val health: Int = 1660
    override val strength: Int = 166
    override val dexterity: Int = 249
    override val defence: Int = 30
    override val luck: Int = 3
}

object CharacterTypeHound : CharacterType() {
    override val type: String = "Hound"
    override val health: Int = 2490
    override val strength: Int = 228
    override val dexterity: Int = 290
    override val defence: Int = 35
    override val luck: Int = 8
}

object CharacterTypeGhost : CharacterType() {
    override val type: String = "Ghost"
    override val health: Int = 830
    override val strength: Int = 83
    override val dexterity: Int = 373
    override val defence: Int = 20
    override val luck: Int = 10
}

object CharacterTypeTentacle : CharacterType() {
    override val type: String = "Tentacle"
    override val health: Int = 2905
    override val strength: Int = 207
    override val dexterity: Int = 269
    override val defence: Int = 50
    override val luck: Int = 3
}

object CharacterTypeOrc : CharacterType() {
    override val type: String = "Orc"
    override val health: Int = 2905
    override val strength: Int = 269
    override val dexterity: Int = 249
    override val defence: Int = 60
    override val luck: Int = 12
}
