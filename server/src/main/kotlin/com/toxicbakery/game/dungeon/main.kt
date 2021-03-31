package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.map.MAP_DB
import com.toxicbakery.logging.Arbor
import com.toxicbakery.logging.Seedling
import io.ktor.application.*
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.mapdb.DB
import org.mapdb.DBMaker

fun Application.main() {
    Arbor.sow(Seedling())
    DungeonApplication(applicationKodein).apply { main() }
}

private val applicationKodein = Kodein {
    bind<DB>(MAP_DB) with instance(
        DBMaker.fileDB("dungeon.db")
            .closeOnJvmShutdown()
            .executorEnable()
            .fileMmapEnable()
            .make()
    )
    extend(commonApplicationKodein)
    import(dungeonServerModule)
}
