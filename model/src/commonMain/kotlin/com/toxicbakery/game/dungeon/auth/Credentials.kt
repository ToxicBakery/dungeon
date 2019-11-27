package com.toxicbakery.game.dungeon.auth

import kotlinx.serialization.Serializable

/**
 * User credentials.
 *
 * TODO Switch to inline classes for username/password once serialization supports it.
 */
@Serializable
data class Credentials(
    val username: String = "",
    val password: String= ""
)
