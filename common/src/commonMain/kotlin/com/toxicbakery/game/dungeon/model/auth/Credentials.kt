package com.toxicbakery.game.dungeon.model.auth

import kotlinx.serialization.Serializable

/**
 * User credentials.
 */
@Serializable
data class Credentials(
    val username: String = "",
    val password: String = ""
)
