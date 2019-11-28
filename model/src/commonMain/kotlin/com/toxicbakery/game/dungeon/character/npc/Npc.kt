package com.toxicbakery.game.dungeon.character.npc

import com.toxicbakery.game.dungeon.character.Character

interface Npc : Character {

    override val canBeKilled: Boolean
        get() = false

}
