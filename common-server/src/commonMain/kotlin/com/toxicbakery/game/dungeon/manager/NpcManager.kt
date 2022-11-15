package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.map.DistanceFilter
import com.toxicbakery.game.dungeon.model.character.Npc
import com.toxicbakery.game.dungeon.model.world.Location
import com.toxicbakery.game.dungeon.persistence.npc.NpcDatabase
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton

private class NpcManagerImpl(
    private val npcDatabase: NpcDatabase
) : NpcManager {

    override suspend fun updateNpc(npc: Npc) {
        npcDatabase.updateNpc(npc)
    }

    override suspend fun getNpcsNear(
        location: Location,
        distanceFilter: DistanceFilter
    ): List<Npc> = npcDatabase.getNpcsNear(location, distanceFilter)
}

interface NpcManager {
    suspend fun updateNpc(npc: Npc)

    suspend fun getNpcsNear(
        location: Location,
        distanceFilter: DistanceFilter
    ): List<Npc>
}

val npcManagerModule = Kodein.Module("npcManagerModule") {
    bind<NpcManager>() with singleton {
        NpcManagerImpl(
            npcDatabase = instance(),
        )
    }
}
