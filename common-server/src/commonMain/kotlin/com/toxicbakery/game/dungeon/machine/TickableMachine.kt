package com.toxicbakery.game.dungeon.machine

interface TickableMachine<S> : Machine<S> {
    /**
     * Update for a game tick.
     */
    suspend fun tick(): TickableMachine<S>
}
