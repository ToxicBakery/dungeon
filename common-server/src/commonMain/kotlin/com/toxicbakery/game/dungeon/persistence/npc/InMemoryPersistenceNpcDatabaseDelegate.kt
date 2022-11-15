package com.toxicbakery.game.dungeon.persistence.npc

import com.toxicbakery.game.dungeon.exception.NoNpcWithIdException
import com.toxicbakery.game.dungeon.map.DistanceFilter
import com.toxicbakery.game.dungeon.model.character.Npc
import com.toxicbakery.game.dungeon.model.world.Location
import com.toxicbakery.game.dungeon.persistence.store.BroadcastChannelStore
import com.toxicbakery.game.dungeon.persistence.store.ChannelStore

internal object InMemoryPersistenceNpcDatabaseDelegate : PersistenceNpcDatabaseDelegate {

    private val idToNpcMapStore: ChannelStore<Map<String, Npc>> = IdToNpcMapStore

    override suspend fun getNpcById(id: String): Npc =
        idToNpcMapStore.value()[id] ?: throw NoNpcWithIdException(id)

    override suspend fun updateNpc(npc: Npc) {
        idToNpcMapStore.modify { map ->
            map + (npc.id to npc)
        }
    }

    override suspend fun createNpc(npc: Npc) {
        idToNpcMapStore.modify { map ->
            map + (npc.id to npc)
        }
    }

    override suspend fun getNpcsNear(
        location: Location,
        distanceFilter: DistanceFilter
    ): List<Npc> = idToNpcMapStore.value().values.toList()

    private object IdToNpcMapStore : BroadcastChannelStore<Map<String, Npc>>(mapOf())
}
