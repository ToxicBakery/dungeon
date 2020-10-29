@file:Suppress("MagicNumber")
package com.toxicbakery.game.dungeon.model.character

import kotlinx.serialization.protobuf.ProtoNumber
import kotlinx.serialization.Serializable

/**
 * Global data about a character unrelated to the world.
 */
@Serializable
data class Global(
    @ProtoNumber(1)
    val hasFinishedRegistration: Boolean = false
)
