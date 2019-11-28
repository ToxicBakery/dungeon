package com.toxicbakery.game.dungeon.character

import com.toxicbakery.game.dungeon.character.type.CharacterType
import com.toxicbakery.game.dungeon.character.type.CharacterTypeNull
import kotlinx.serialization.Serializable

@Serializable
data class Player(
    override val id: Int = 0,
    override val name: String = "",
    override val characterType: CharacterType = CharacterTypeNull
) : Character
