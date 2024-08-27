package com.toxicbakery.game.dungeon.machine.command.processor

import com.toxicbakery.game.dungeon.defaults.AnimalGenerator
import com.toxicbakery.game.dungeon.defaults.BaseAnimal
import com.toxicbakery.game.dungeon.machine.ProcessorMachine
import com.toxicbakery.game.dungeon.machine.command.CommandMachine
import com.toxicbakery.game.dungeon.machine.command.processor.ProcessorSpawn.Companion.COMMAND
import com.toxicbakery.game.dungeon.manager.CommunicationManager
import com.toxicbakery.game.dungeon.manager.NpcManager
import com.toxicbakery.game.dungeon.manager.PlayerManager
import com.toxicbakery.game.dungeon.model.Lookable
import com.toxicbakery.game.dungeon.model.session.GameSession
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

private class ProcessorSpawnImpl(
    private val commandMachine: CommandMachine,
    private val playerManager: PlayerManager,
    private val communicationManager: CommunicationManager,
    private val npcManager: NpcManager,
    private val animalGenerator: AnimalGenerator,
) : ASingleStateProcessor(), ProcessorSpawn {

    override val name: String = COMMAND

    override suspend fun acceptMessage(
        gameSession: GameSession,
        message: String
    ): ProcessorMachine<*> = try {
        val baseAnimal = BaseAnimal.valueOf(message)
        spawnAnimal(baseAnimal, gameSession)

        // TODO Support spawning other npc types
    } catch (e: IllegalArgumentException) {
        gameSession.sendMessage("Invalid animal: ${BaseAnimal.names}")
        commandMachine
    }

    private suspend fun spawnAnimal(
        baseAnimal: BaseAnimal,
        gameSession: GameSession,
    ): ProcessorMachine<*> {
        val player: Lookable.Player = playerManager.getPlayerByGameSession(gameSession)
        if (player.isAdmin) {
            communicationManager.notify(
                player = player,
                message = "You have conjured a ${baseAnimal.name}",
            )

            val animal = animalGenerator.create(baseAnimal, location = player.location)
            npcManager.createNpc(animal)
        } else {
            // FIXME Need to throw a permissions exception that gets handled globally
            communicationManager.notify(
                player = player,
                message = "You have no power here!"
            )
        }
        return commandMachine
    }
}

interface ProcessorSpawn : SingleStateProcessor {
    companion object {
        const val COMMAND = "spawn"
    }
}

val processorSpawnModule = DI.Module("processorSpawnModule") {
    bind<CommandRef>(COMMAND) with provider {
        CommandRef(
            name = COMMAND,
            isPrivileged = true,
            processor = { commandMachine ->
                ProcessorSpawnImpl(
                    commandMachine = commandMachine,
                    playerManager = instance(),
                    communicationManager = instance(),
                    npcManager = instance(),
                    animalGenerator = instance(),
                )
            }
        )
    }
}
