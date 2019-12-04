package com.toxicbakery.game.dungeon.util

import io.ktor.http.cio.websocket.Frame

fun textFrame(data: String): Frame.Text = Frame.Text(data)
