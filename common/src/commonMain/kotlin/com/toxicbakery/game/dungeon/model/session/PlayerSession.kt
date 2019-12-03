package com.toxicbakery.game.dungeon.model.session

import com.toxicbakery.game.dungeon.model.character.Player

data class PlayerSession(
    val player: Player,
    val session: GameSession
)
