package com.toxicbakery.game.dungeon.persistence.npc

import com.toxicbakery.game.dungeon.map.DistanceFilter
import com.toxicbakery.game.dungeon.model.ILookable
import com.toxicbakery.game.dungeon.model.character.Npc
import com.toxicbakery.game.dungeon.model.world.Location
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

private class NpcDatabaseImpl(
    private val persistenceDelegate: PersistenceNpcDatabaseDelegate
) : NpcDatabase {
    override suspend fun getNpcById(id: String): Npc = persistenceDelegate.getNpcById(id)

    override suspend fun updateNpc(npc: Npc) = persistenceDelegate.updateNpc(npc)

    override suspend fun createNpc(npc: Npc) = persistenceDelegate.createNpc(npc)

    override suspend fun getNpcsNear(
        location: Location,
        distanceFilter: DistanceFilter
    ): List<ILookable> = persistenceDelegate.getNpcsNear(location, distanceFilter)
}

interface NpcDatabase {

    suspend fun getNpcById(id: String): Npc

    suspend fun updateNpc(npc: Npc)

    suspend fun createNpc(npc: Npc)

    suspend fun getNpcsNear(location: Location, distanceFilter: DistanceFilter): List<ILookable>
}

val npcDatabaseModule = DI.Module("npcDatabaseModule") {
    bind<NpcDatabase>() with singleton {
        NpcDatabaseImpl(
            persistenceDelegate = InMemoryPersistenceNpcDatabaseDelegate
        )
    }
}
