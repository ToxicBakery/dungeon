package com.toxicbakery.game.dungeon.model.character.npc

import com.toxicbakery.game.dungeon.model.character.Character

interface Npc : Character {

    override val canBeKilled: Boolean
        get() = false

}
