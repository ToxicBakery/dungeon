package com.toxicbakery.game.dungeon.model.client

import com.toxicbakery.game.dungeon.model.client.ClientMessage.*
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import kotlin.test.Test
import kotlin.test.assertEquals

@UnstableDefault
class ClientMessageTest {

    @Test
    fun serialization_UserMessage() {
        val message: ClientMessage = UserMessage("UserMessage")
        val json = Json.plain.toJson(ClientMessage.serializer(), message)
        val deserialized: ClientMessage = Json.plain.fromJson(ClientMessage.serializer(), json)
        assertEquals(message, deserialized)
    }

    @Test
    fun serialization_serverMessage() {
        val message: ClientMessage = ServerMessage("ServerMessage")
        val json = Json.plain.toJson(ClientMessage.serializer(), message)
        val deserialized: ClientMessage = Json.plain.fromJson(ClientMessage.serializer(), json)
        assertEquals(message, deserialized)
    }

    @Test
    fun serialization_list() {
        val messageList: List<ClientMessage> = listOf(
            UserMessage("UserMessage"),
            ServerMessage("ServerMessage"),
            PlayerDataMessage(PlayerData())
        )

        val json = Json.plain.toJson(ClientMessage.serializer().list, messageList)
        val deserialized: List<ClientMessage> = Json.plain.fromJson(ClientMessage.serializer().list, json)
        assertEquals(messageList, deserialized)
    }

}