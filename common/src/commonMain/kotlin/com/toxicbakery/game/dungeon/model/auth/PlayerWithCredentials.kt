package com.toxicbakery.game.dungeon.model.auth

import com.toxicbakery.game.dungeon.model.character.Player

data class PlayerWithCredentials(
    val player: Player,
    val credentials: Credentials = Credentials()
)
