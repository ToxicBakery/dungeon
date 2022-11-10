package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.map.MapLegend
import com.toxicbakery.game.dungeon.model.Lookable.Animal
import com.toxicbakery.game.dungeon.model.Lookable.Creature
import com.toxicbakery.game.dungeon.model.Lookable.Npc
import com.toxicbakery.game.dungeon.model.Lookable.Player
import com.toxicbakery.game.dungeon.model.client.ClientMessage
import com.toxicbakery.game.dungeon.model.client.ClientMessage.DirectedLookMessage
import com.toxicbakery.game.dungeon.model.client.ClientMessage.MapMessage
import com.toxicbakery.game.dungeon.model.client.ClientMessage.PlayerDataMessage
import com.toxicbakery.game.dungeon.model.client.ClientMessage.ServerMessage
import com.toxicbakery.game.dungeon.model.client.ClientMessage.UserMessage
import com.toxicbakery.game.dungeon.model.client.ExpectedResponseType
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
        val locationDescription = when (MapLegend.representingByte(message.lookLocation.mapLegendByte)) {
            MapLegend.FOREST_1,
            MapLegend.FOREST_2 -> "A few trees here"

            MapLegend.FOREST_3,
            MapLegend.FOREST_4 -> "You peer through the thick woods"

            MapLegend.OCEAN -> "The dark ocean peers back at you"
            MapLegend.RIVER -> "A raging river runs here"
            MapLegend.DESERT -> "An uncomfortable desert"
            MapLegend.PLAIN -> "Fields of grass"
            MapLegend.BEACH -> "You hear the waves crashing against the beach"
            MapLegend.LAKE -> "A lake, maybe it has fish"
            MapLegend.MOUNTAIN -> "Mountains, difficult and dangerous"
            else -> "You have no idea what you're looking at..."
        }

        val output = locationDescription + message.lookLocation
            .lookables
            .map { displayable ->
                when (displayable) {
                    is Animal ->
                        if (displayable.isPassive) "A ${displayable.name} wanders around"
                        else "A ${displayable.name} is on the hunt"

                    is Creature ->
                        if (displayable.isPassive) "A ${displayable.name} wanders around"
                        else "A ${displayable.name} is charging towards you"

                    is Npc -> "You see ${displayable.name} looking back at you"
                    is Player -> "You see ${displayable.name} looking back at you"
                    else -> ""
                }
            }
            .filter(String::isNotEmpty)
            .joinToString(separator = "\n\t")
            .let { displayables -> if (displayables.isNotEmpty()) "\n$displayables" else "" }

        handleMessage(
            UserMessage(
                message = output.replace("\n", "<br/>")
                    .replace("\t", "&emsp;")
            )
        )
    }

    private fun recycleMessageElement(element: Element, message: String) {
        element.innerHTML = message
    }

    private fun createMessageElement(message: String): Element =
        document.createElement("p") { innerHTML = message }

    private fun ServerMessage.toHtml(): String = message.replace("\n", "<br/>")

    companion object {
        private const val PERCENT_MULTIPLIER = 100.0
    }
}
