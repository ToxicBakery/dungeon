package com.toxicbakery.game.dungeon.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface Identifiable {
    val id: String

    @OptIn(ExperimentalUuidApi::class)
    companion object {
        fun generateId(): String = Uuid.random().toString()
    }
}
