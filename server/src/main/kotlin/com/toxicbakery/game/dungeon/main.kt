package com.toxicbakery.game.dungeon

import com.toxicbakery.logging.Arbor
import com.toxicbakery.logging.Seedling
import io.ktor.application.Application
import org.kodein.di.DI

fun Application.main() {
    Arbor.sow(Seedling())
    DungeonApplication(applicationKodein).apply { main() }
}

private val applicationKodein = DI {
    extend(commonApplicationKodein)
    import(dungeonServerModule)
}
