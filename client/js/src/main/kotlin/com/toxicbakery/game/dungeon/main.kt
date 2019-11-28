package com.toxicbakery.game.dungeon

import com.toxicbakery.logging.Arbor
import org.w3c.dom.Document
import org.w3c.dom.HTMLInputElement
import kotlin.browser.window

private const val KEY_ENTER = 13

private val document: Document
    get() = window.document

private val input: HTMLInputElement
    get() = document.getElementById("commandInput") as HTMLInputElement

fun main() {
    Arbor.sow(Seedling())
    fun onLoad() {
        val messages = document.getElementById("messages")
            ?: error("Missing terminal window")

        val terminal = Terminal(messages)
        val client = SocketClient(
            host = window.location.host,
            terminal = terminal
        )
        client.start()

        fun sendMessage() {
            val inputText = input.value
            input.value = ""
            client.sendMessage(inputText)
        }

        val sendButton = document.getElementById("sendButton") as HTMLInputElement
        sendButton.onclick = { sendMessage() }
        input.onkeydown = { e -> if (e.keyCode == KEY_ENTER) sendMessage() }
    }

    window.onload = { onLoad() }
}
