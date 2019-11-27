package com.toxicbakery.game.dungeon.auth

import com.toxicbakery.game.dungeon.Player

data class PlayerWithCredentials(
    val player: Player,
    val credentials: Credentials = Credentials()
)