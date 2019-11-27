package com.toxicbakery.game.dungeon.model.session

import com.toxicbakery.game.dungeon.Player
import com.toxicbakery.game.dungeon.model.session.GameSession

data class PlayerSession(
    val player: Player,
    val session: GameSession
)
