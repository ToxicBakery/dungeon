package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.model.client.ClientMessage
import com.toxicbakery.game.dungeon.model.client.ClientMessage.*
import com.toxicbakery.game.dungeon.model.client.ExpectedResponseType
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

    private val windowRenderer: WindowRenderer = WindowRenderer(::handleMessage)
    private var expectedResponseType: ExpectedResponseType = ExpectedResponseType.Normal

    fun displayMessage(message: ClientMessage) = when (message) {
        is UserMessage -> handleMessage(message)
        is ServerMessage -> handleMessage(message)
        is PlayerDataMessage -> handleMessage(message)
        is MapMessage -> windowRenderer.render(message.window)
    }

    private fun handleMessage(message: UserMessage) {
        val output = if (expectedResponseType == ExpectedResponseType.Secure) "* * * *"
        else message.message
        handleMessage("> $output<br/><br/>")
    }

    private fun handleMessage(message: ServerMessage) {
        expectedResponseType = message.expectedResponseType
        handleMessage(message.toHtml())
    }

    private fun handleMessage(message: PlayerDataMessage) {
        val playerData = message.playerData
        val loc = playerData.location
        val maxHealth = if (playerData.maxHealth == 0) 1 else playerData.maxHealth
        val hp = round(playerData.stats.health.toDouble() / maxHealth.toDouble() * PERCENT_MULTIPLIER)
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

    companion object {
        private const val PERCENT_MULTIPLIER = 100.0
    }

}
