package com.toxicbakery.game.dungeon

import com.toxicbakery.logging.Arbor
import com.toxicbakery.logging.Seedling
import io.ktor.application.Application
import org.kodein.di.Kodein

fun Application.main() {
    Arbor.sow(Seedling())
    DungeonApplication(applicationKodein).apply { main() }
}

private val applicationKodein = Kodein {
    extend(commonApplicationKodein)
    import(dungeonServerModule)
}
