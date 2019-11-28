package com.toxicbakery.game.dungeon.model.session

interface GameSession {

    val isClosed: Boolean

    /**
     * A unique identifier representing this session.
     */
    val sessionId: PlayerSessionId

    /**
     * Write data back to the client
     */
    suspend fun send(
        msg: String,
        inputResponseType: InputResponseType = InputResponseType.Normal
    )

    /**
     * Terminate the session.
     */
    suspend fun close()

    enum class InputResponseType {
        /**
         * Input field should be normal
         */
        Normal,

        /**
         * Input field should be masked for secure entry
         */
        Secure
    }

}
