@file:Suppress("MagicNumber")

package com.toxicbakery.game.dungeon.model.character

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/**
 * Global data about a character unrelated to the world.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Global(
    @ProtoNumber(1)
    val hasFinishedRegistration: Boolean = false
)
