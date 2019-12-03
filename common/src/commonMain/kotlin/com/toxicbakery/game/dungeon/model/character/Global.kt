@file:Suppress("MagicNumber")
package com.toxicbakery.game.dungeon.model.character

import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable

/**
 * Global data about a character unrelated to the world.
 */
@Serializable
data class Global(
    @SerialId(1)
    val hasFinishedRegistration: Boolean = false
)
