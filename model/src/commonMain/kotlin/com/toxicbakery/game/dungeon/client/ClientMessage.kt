package com.toxicbakery.game.dungeon.client

import kotlinx.serialization.Serializable

/**
 * Message base type. Client messages represent data transfers between the client and server.
 */
sealed class ClientMessage {

    /**
     * Message sent by the user.
     */
    @Serializable
    data class UserMessage(
        val message: String
    ) : ClientMessage()

    @Serializable
    data class ServerMessage(
        val message: String,
        val expectedResponseType: ExpectedResponseType = ExpectedResponseType.Normal
    ) : ClientMessage()

}

enum class ExpectedResponseType {
    Normal,
    Secure
}
