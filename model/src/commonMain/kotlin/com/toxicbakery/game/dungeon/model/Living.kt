package com.toxicbakery.game.dungeon.model

import com.toxicbakery.game.dungeon.model.character.stats.Stats

interface Living {

    /**
     * Determine if this thing can be attacked and killed.
     */
    val canBeKilled: Boolean
        get() = true

    /**
     * Check if this thing has died.
     */
    val isDead: Boolean
        get() = stats.health <= 0

    /**
     * Stats of this thing for interactions with the world. These can be modified by events such as fighting or
     * consuming items.
     */
    val stats: Stats

    /**
     * Base stats of this thing.
     */
    val statsBase: Stats
}
