package com.toxicbakery.game.dungeon.persistence.npc

import com.toxicbakery.game.dungeon.exception.NoNpcWithIdException
import com.toxicbakery.game.dungeon.map.DistanceFilter
import com.toxicbakery.game.dungeon.model.ILookable
import com.toxicbakery.game.dungeon.model.character.Npc
import com.toxicbakery.game.dungeon.model.world.Location
import com.toxicbakery.game.dungeon.persistence.store.BroadcastChannelStore
import com.toxicbakery.game.dungeon.persistence.store.ChannelStore

internal object InMemoryPersistenceNpcDatabaseDelegate : PersistenceNpcDatabaseDelegate {

    private val idToNpcMapStore: ChannelStore<Map<String, ILookable>> = IdToNpcMapStore

    override suspend fun getNpcById(id: String): Npc =
        idToNpcMapStore.value()[id] as Npc? ?: throw NoNpcWithIdException(id)

    override suspend fun updateNpc(npc: Npc) {
        idToNpcMapStore.modify { map ->
            map + (npc.id to (npc as ILookable))
        }
    }

    override suspend fun createNpc(npc: Npc) {
        idToNpcMapStore.modify { map ->
            map + (npc.id to (npc as ILookable))
        }
    }

    override suspend fun getNpcsNear(
        location: Location,
        distanceFilter: DistanceFilter
    ): List<ILookable> = idToNpcMapStore.value()
        .values
        .filter { lookable -> distanceFilter.nearby(location, lookable.location) }
        .toList()

    override suspend fun getNpcCount(): Int = idToNpcMapStore.value().size

    private object IdToNpcMapStore : BroadcastChannelStore<Map<String, ILookable>>(mapOf())
}
