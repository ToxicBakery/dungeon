package com.toxicbakery.game.dungeon.machine

import com.toxicbakery.game.dungeon.model.session.GameSession

/**
 * A state machine representation.
 *
 * @param S the type of states represented by this machine
 */
interface Machine<S> {

    val name: String

    val currentState: S

    /**
     * Take a message and return a machine to handle future messages.
     */
    suspend fun acceptMessage(
        gameSession: GameSession,
        message: String
    ): Machine<*> = this

    /**
     * Start the machine
     */
    suspend fun initMachine(gameSession: GameSession):Machine<S> = this

}
