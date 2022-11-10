package com.toxicbakery.game.dungeon

import com.toxicbakery.logging.Arbor
import kotlinx.browser.window
import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement

private const val CONNECTION_MONITOR_INTERVAL = 500
private const val KEY_ENTER = 13

private fun getElementById(id: String): Element =
    document.getElementById(id) ?: error("Missing $id element")

private val document: Document
    get() = window.document

private val input: HTMLInputElement
    get() = getElementById("commandInput") as HTMLInputElement

private val connectionStatusElement: HTMLElement
    get() = getElementById("connectionStatus") as HTMLElement

fun setConnected(connected: Boolean) = connectionStatusElement.apply {
    if (connected) {
        textContent = "Connected"
        removeClass("disconnected")
        addClass("connected")
    } else {
        textContent = "Disconnected"
        removeClass("connected")
        addClass("disconnected")
    }
}

fun main() {
    Arbor.sow(Seedling())
    document.onclick = { _ -> input.focus() }
    window.onload = { onLoad() }
}

fun onLoad() {
    val terminal = Terminal(
        healthElement = getElementById("healthValue"),
        locationElement = getElementById("locationValue"),
        messagesElement = getElementById("messages")
    )
    val client = SocketClient(
        host = window.location.host,
        terminal = terminal
    )

    startConnectionMonitor(client)
    client.start()

    fun sendMessage() {
        val inputText = input.value
        if (inputText.isEmpty()) return
        input.value = ""
        client.sendMessage(message = inputText)
    }

    val sendButton = document.getElementById("sendButton") as HTMLInputElement
    sendButton.onclick = { sendMessage() }
    input.onkeydown = { e -> if (e.keyCode == KEY_ENTER) sendMessage() }
}

private fun startConnectionMonitor(client: SocketClient) {
    val connectedCallback = { setConnected(client.isConnected) }
    // Init the display
    connectedCallback()
    window.setInterval(connectedCallback, CONNECTION_MONITOR_INTERVAL)
}
