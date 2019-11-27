package com.toxicbakery.game.dungeon

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: Int = 0,
    val name: String = ""
)
