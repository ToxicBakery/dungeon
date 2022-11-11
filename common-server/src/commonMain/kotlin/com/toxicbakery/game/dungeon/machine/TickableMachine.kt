package com.toxicbakery.game.dungeon.machine

interface TickableMachine<S> : Machine<S> {
    /**
     * Update for a game tick.
     */
    fun tick(): TickableMachine<S>
}
