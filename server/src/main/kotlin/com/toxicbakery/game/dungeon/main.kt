package com.toxicbakery.game.dungeon

import com.toxicbakery.logging.Arbor
import com.toxicbakery.logging.Seedling

fun main(args: Array<String>) {
    Arbor.sow(Seedling())
    io.ktor.server.netty.EngineMain.main(args)
}
