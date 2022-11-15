package com.toxicbakery.game.dungeon.persistence.npc

import com.toxicbakery.game.dungeon.map.DistanceFilter
import com.toxicbakery.game.dungeon.model.character.Npc
import com.toxicbakery.game.dungeon.model.world.Location
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.singleton

private class NpcDatabaseImpl(
    private val persistenceDelegate: PersistenceNpcDatabaseDelegate
) : NpcDatabase {
    override suspend fun getNpcById(id: String): Npc = persistenceDelegate.getNpcById(id)

    override suspend fun updateNpc(npc: Npc) = persistenceDelegate.updateNpc(npc)

    override suspend fun createNpc(npc: Npc) = persistenceDelegate.createNpc(npc)

    override suspend fun getNpcsNear(
        location: Location,
        distanceFilter: DistanceFilter
    ): List<Npc> = persistenceDelegate.getNpcsNear(location, distanceFilter)
}

interface NpcDatabase {

    suspend fun getNpcById(id: String): Npc

    suspend fun updateNpc(npc: Npc)

    suspend fun createNpc(npc: Npc)

    suspend fun getNpcsNear(location: Location, distanceFilter: DistanceFilter): List<Npc>
}

val npcDatabaseModule = Kodein.Module("npcDatabaseModule") {
    bind<NpcDatabase>() with singleton {
        NpcDatabaseImpl(
            persistenceDelegate = InMemoryPersistenceNpcDatabaseDelegate
        )
    }
}
