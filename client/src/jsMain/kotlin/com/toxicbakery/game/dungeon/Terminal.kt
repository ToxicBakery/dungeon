package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.client.ClientMessage.ServerMessage
import com.toxicbakery.game.dungeon.client.ExpectedResponseType
import kotlinx.html.dom.create
import kotlinx.html.p
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import kotlin.browser.document

class Terminal(
    private val terminalMessages: Element,
    private val bufferSize: Int = 500
) {

    var expectedResponseType: ExpectedResponseType = ExpectedResponseType.Normal

    fun displayMessage(message: ServerMessage) {
        expectedResponseType = message.expectedResponseType
        terminalMessages.append(
            if (terminalMessages.childElementCount < bufferSize) createMessageElement(message)
            else {
                val element = terminalMessages.children[0]!!
                terminalMessages.removeChild(element)
                recycleMessageElement(element, message)
                element
            }
        )
        terminalMessages.scrollTo(0.0, terminalMessages.scrollHeight.toDouble())
    }

    private fun recycleMessageElement(element: Element, serverMessage: ServerMessage) {
        element.innerHTML = serverMessage.toHtml()
    }

    private fun createMessageElement(serverMessage: ServerMessage): HTMLElement =
        document.create.p("message").apply {
            innerHTML = serverMessage.toHtml()
        }

    private fun ServerMessage.toHtml(): String = message.replace("\n", "<br/>")


}
