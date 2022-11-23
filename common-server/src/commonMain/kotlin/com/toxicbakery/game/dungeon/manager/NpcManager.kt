package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.machine.TickableMachine
import com.toxicbakery.game.dungeon.machine.ai.AggressiveAnimalMachine
import com.toxicbakery.game.dungeon.machine.ai.PassiveAnimalMachine
import com.toxicbakery.game.dungeon.map.DistanceFilter
import com.toxicbakery.game.dungeon.model.ILookable
import com.toxicbakery.game.dungeon.model.Lookable.Animal
import com.toxicbakery.game.dungeon.model.character.Npc
import com.toxicbakery.game.dungeon.model.world.Location
import com.toxicbakery.game.dungeon.persistence.npc.NpcDatabase
import com.toxicbakery.game.dungeon.persistence.store.GameClock
import com.toxicbakery.game.dungeon.tickScope
import kotlinx.coroutines.launch
import org.kodein.di.*

private class NpcManagerImpl(
    private val npcDatabase: NpcDatabase,
    gameClock: GameClock,
    private val aggressiveAnimalMachineBuilder: (AnimalInit) -> AggressiveAnimalMachine,
    private val passiveAnimalMachineBuilder: (AnimalInit) -> PassiveAnimalMachine,
) : NpcManager {

    private val npcMachinesManager = NpcMachinesManager(gameClock)

    override suspend fun createNpc(npc: Npc) {
        npcMachinesManager.addMachine(
            when (npc) {
                is Animal ->
                    if (npc.isPassive) createPassiveAnimalMachine(npc)
                    else createAggressiveAnimalMachine(npc)

                else -> TODO()
            }
        )
        npcDatabase.createNpc(npc)
    }

    override suspend fun updateNpc(npc: Npc) {
        npcDatabase.updateNpc(npc)
    }

    override suspend fun getNpcsNear(
        location: Location,
        distanceFilter: DistanceFilter
    ): List<ILookable> = npcDatabase.getNpcsNear(location, distanceFilter)

    override suspend fun getNpcCount(): Int = npcDatabase.getNpcCount()

    private fun createAggressiveAnimalMachine(animal: Animal) = aggressiveAnimalMachineBuilder(
        AnimalInit(
            npcManager = this,
            animal = animal,
        )
    )

    private fun createPassiveAnimalMachine(animal: Animal) = passiveAnimalMachineBuilder(
        AnimalInit(
            npcManager = this,
            animal = animal,
        )
    )
}

data class AnimalInit(
    val npcManager: NpcManager,
    val animal: Animal,
)

private class NpcMachinesManager(
    private val gameClock: GameClock,
) {

    private val tickJob = tickScope.launch {
        gameClock.gameTickFlow
            .collect { tick() }
    }

    private val machines: MutableMap<String, TickableMachine<*>> = mutableMapOf()

    fun shutdown() = tickJob.cancel()

    fun addMachine(machine: TickableMachine<*>) {
        tickScope.launch {
            machines[machine.instanceId] = machine
        }
    }

    private suspend fun tick() {
        machines.values
            .toList()
            .forEach { machine ->
                val newMachine = machine.tick()
                machines[newMachine.instanceId] = newMachine
            }
    }
}

interface NpcManager {
    suspend fun createNpc(npc: Npc)

    suspend fun updateNpc(npc: Npc)

    suspend fun getNpcsNear(
        location: Location,
        distanceFilter: DistanceFilter
    ): List<ILookable>

    suspend fun getNpcCount(): Int
}

val npcManagerModule = DI.Module("npcManagerModule") {
    bind<NpcManager>() with singleton {
        NpcManagerImpl(
            npcDatabase = instance(),
            gameClock = instance(),
            aggressiveAnimalMachineBuilder = factory(),
            passiveAnimalMachineBuilder = factory(),
        )
    }
}
