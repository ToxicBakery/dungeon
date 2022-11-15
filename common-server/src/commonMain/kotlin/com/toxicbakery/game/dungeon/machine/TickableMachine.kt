package com.toxicbakery.game.dungeon.machine

interface TickableMachine<S> : Machine<S> {

    /**
     * ID of this machines instance.
     */
    val instanceId: String

    /**
     * Update for a game tick.
     */
    suspend fun tick(): TickableMachine<S>
}
