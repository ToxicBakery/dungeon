package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.map.MapLegend
import com.toxicbakery.game.dungeon.model.client.ClientMessage
import com.toxicbakery.game.dungeon.model.client.ClientMessage.DirectedLookMessage
import com.toxicbakery.game.dungeon.model.client.ClientMessage.MapMessage
import com.toxicbakery.game.dungeon.model.client.ClientMessage.PlayerDataMessage
import com.toxicbakery.game.dungeon.model.client.ClientMessage.ServerMessage
import com.toxicbakery.game.dungeon.model.client.ClientMessage.UserMessage
import com.toxicbakery.game.dungeon.model.client.ExpectedResponseType
import com.toxicbakery.game.dungeon.ui.locationDescription
import com.toxicbakery.game.dungeon.ui.lookableDescriptions
import kotlin.math.round
import kotlinx.browser.document
import kotlinx.dom.createElement
import org.w3c.dom.Element
import org.w3c.dom.get

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
        is DirectedLookMessage -> handleMessage(message)
        is MapMessage -> windowRenderer.render(message.window)
    }

    private fun handleMessage(message: UserMessage) {
        val output = if (expectedResponseType == ExpectedResponseType.Secure) "* * * *"
        else message.message
        handleMessage("> $output${HTML_LINE_BREAK.repeat(2)}")
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
        healthElement.textContent = "$hp%"
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

    private fun handleMessage(message: DirectedLookMessage) {
        val mapLegend = MapLegend.representingByte(message.lookLocation.mapLegendByte)
        val locationDescription = mapLegend.locationDescription()
        val lookableDescriptions = message.lookLocation.lookables.lookableDescriptions()
        val outputMessage = (locationDescription + lookableDescriptions)
            .replace("\n", HTML_LINE_BREAK)
            .replace("\t", HTML_TAB)

        handleMessage(
            UserMessage(
                message = outputMessage
            )
        )
    }

    private fun recycleMessageElement(element: Element, message: String) {
        element.innerHTML = message
    }

    private fun createMessageElement(message: String): Element =
        document.createElement("p") { innerHTML = message }

    private fun ServerMessage.toHtml(): String = message.replace("\n", HTML_LINE_BREAK)

    companion object {
        private const val PERCENT_MULTIPLIER = 100.0
        private const val HTML_LINE_BREAK = "<br/>"
        private const val HTML_TAB = "&emsp;"
    }
}
