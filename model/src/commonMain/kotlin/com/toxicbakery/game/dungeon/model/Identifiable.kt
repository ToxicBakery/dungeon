package com.toxicbakery.game.dungeon.model

import com.benasher44.uuid.uuid4

interface Identifiable {
    val id: String

    companion object {
        fun generateId(): String = uuid4().toString()
    }
}
