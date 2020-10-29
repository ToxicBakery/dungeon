package com.toxicbakery.game.dungeon.model.client

import com.toxicbakery.game.dungeon.model.client.ClientMessage.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ClientMessageTest {

    @Test
    fun serialization_UserMessage() {
        val message: ClientMessage = UserMessage("UserMessage")
        val json = Json.Default.encodeToString(ClientMessage.serializer(), message)
        val deserialized: ClientMessage = Json.Default.decodeFromString(ClientMessage.serializer(), json)
        assertEquals(message, deserialized)
    }

    @Test
    fun serialization_serverMessage() {
        val message: ClientMessage = ServerMessage("ServerMessage")
        val json = Json.Default.encodeToString(ClientMessage.serializer(), message)
        val deserialized: ClientMessage = Json.Default.decodeFromString(ClientMessage.serializer(), json)
        assertEquals(message, deserialized)
    }

    @Test
    fun serialization_list() {
        val messageList: List<ClientMessage> = listOf(
            UserMessage("UserMessage"),
            ServerMessage("ServerMessage"),
            PlayerDataMessage(PlayerData())
        )

        val clientMessageListSerializer = ListSerializer(ClientMessage.serializer())
        val json = Json.Default.encodeToString(clientMessageListSerializer, messageList)
        val deserialized: List<ClientMessage> = Json.Default.decodeFromString(clientMessageListSerializer, json)
        assertEquals(messageList, deserialized)
    }

}
