package com.toxicbakery.game.dungeon.machine

import com.toxicbakery.game.dungeon.model.session.GameSession

interface ProcessorMachine<S> : Machine<S> {

    override suspend fun initMachine(gameSession: GameSession): ProcessorMachine<S> = this

    /**
     * Take a message and return a machine to handle future messages.
     */
    suspend fun acceptMessage(
        gameSession: GameSession,
        message: String
    ): ProcessorMachine<*> = this
}
