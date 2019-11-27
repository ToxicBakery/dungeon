package com.toxicbakery.game.dungeon.model.session

interface GameSession {

    val sessionId: PlayerSessionId

    val gameSessionState: GameSessionState

    suspend fun send(msg: String)

    suspend fun close()

    /**
     * Copy the game session returning it with the new game session state applied.
     */
    fun setGameSessionState(gameSessionState: GameSessionState): GameSession

}

sealed class GameSessionState {
    object Init : GameSessionState()
    object Authenticating : GameSessionState()
    object Authenticated : GameSessionState()
}
