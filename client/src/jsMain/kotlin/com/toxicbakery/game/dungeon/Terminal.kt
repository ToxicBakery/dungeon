package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.client.ClientMessage
import com.toxicbakery.game.dungeon.client.ClientMessage.*
import com.toxicbakery.game.dungeon.client.ExpectedResponseType
import kotlinx.html.dom.create
import kotlinx.html.p
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.math.round

class Terminal(
    private val healthElement: Element,
    private val locationElement: Element,
    private val messagesElement: Element,
    private val bufferSize: Int = 500
) {

    var expectedResponseType: ExpectedResponseType = ExpectedResponseType.Normal

    fun displayMessage(message: ClientMessage) {
        when (message) {
            is UserMessage -> handleMessage(message)
            is ServerMessage -> handleMessage(message)
            is PlayerDataMessage -> handleMessage(message)
        }
    }

    private fun handleMessage(message: UserMessage) {
        handleMessage("> ${message.message}<br/><br/>")
    }

    private fun handleMessage(message: ServerMessage) {
        expectedResponseType = message.expectedResponseType
        handleMessage(message.toHtml())
    }

    private fun handleMessage(message: PlayerDataMessage) {
        val playerData = message.playerData
        val loc = playerData.location
        val hp = round(playerData.stats.health.toDouble() / playerData.maxHealth.toDouble() * 100.0)
        healthElement.textContent = "${hp}%"
        locationElement.textContent = "(${loc.x}, ${loc.y})"
    }

    private fun handleMessage(message: String) {
        messagesElement.append(
            if (messagesElement.childElementCount < bufferSize) createMessageElement(message)
            else {
                val element = messagesElement.children[0]!!
                messagesElement.removeChild(element)
                recycleMessageElement(element, message)
                element
            }
        )
        messagesElement.scrollTo(0.0, messagesElement.scrollHeight.toDouble())
    }

    private fun recycleMessageElement(element: Element, message: String) {
        element.innerHTML = message
    }

    private fun createMessageElement(message: String): HTMLElement =
        document.create.p("message").apply { innerHTML = message }

    private fun ServerMessage.toHtml(): String = message.replace("\n", "<br/>")

}
