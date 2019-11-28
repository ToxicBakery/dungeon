package com.toxicbakery.game.dungeon

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

    fun displayMessage(message: String) {
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

    private fun recycleMessageElement(element: Element, message: String) {
        element.innerHTML = message.toHtml()
    }

    private fun createMessageElement(message: String): HTMLElement =
        document.create.p("message").apply {
            innerHTML = message.toHtml()
        }

    private fun String.toHtml(): String = replace("\n", "<br/>")

}
