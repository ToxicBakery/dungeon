package com.toxicbakery.game.dungeon.model.session

import com.toxicbakery.game.dungeon.model.Lookable.Player

data class PlayerSession(
    val player: Player,
    val session: GameSession
)
