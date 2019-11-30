package com.toxicbakery.game.dungeon.util

import io.ktor.http.cio.websocket.Frame

fun binaryFrame(data: ByteArray): Frame.Binary = Frame.Binary(true, data)
