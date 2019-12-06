package com.toxicbakery.game.dungeon.machine.command.processor

import com.toxicbakery.game.dungeon.machine.Machine
import com.toxicbakery.game.dungeon.machine.command.CommandMachine
import com.toxicbakery.game.dungeon.machine.command.processor.ProcessorWalk.Companion.COMMAND
import com.toxicbakery.game.dungeon.manager.PlayerManager
import com.toxicbakery.game.dungeon.manager.WorldManager
import com.toxicbakery.game.dungeon.map.model.Direction
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.logging.Arbor
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private class ProcessorWalkImpl(
    private val commandMachine: CommandMachine,
    private val processorLook: CommandProcessor<*>,
    private val playerManager: PlayerManager,
    private val worldManager: WorldManager
) : ASingleStateProcessor(), ProcessorWalk {

    override val name: String = COMMAND

    override suspend fun acceptMessage(
        gameSession: GameSession,
        message: String
    ): Machine<*> = directionMap[message]
        ?.let { direction -> walkDirection(gameSession, direction) }
        ?: invalidDirection(gameSession)

    private suspend fun invalidDirection(gameSession: GameSession): Machine<*> {
        gameSession.sendMessage("Huh?")
        return commandMachine
    }

    private suspend fun walkDirection(
        gameSession: GameSession,
        direction: Direction
    ): Machine<*> {
        val player = playerManager.getPlayerByGameSession(gameSession)
        val location = worldManager.getTravelLocation(player, direction)
        playerManager.updatePlayer(
            player.copy(location = location),
            gameSession
        )

        // Redisplay the map
        return processorLook.acceptMessage(gameSession, ProcessorLook.COMMAND)
    }

    companion object {
        private const val DIR_N = "n"
        private const val DIR_S = "s"
        private const val DIR_W = "w"
        private const val DIR_E = "e"
        private const val DIR_N_LONG = "north"
        private const val DIR_S_LONG = "south"
        private const val DIR_W_LONG = "west"
        private const val DIR_E_LONG = "east"

        private val directionMap: Map<String, Direction> = mapOf(
            DIR_N to Direction.NORTH,
            DIR_N_LONG to Direction.NORTH,
            DIR_S to Direction.SOUTH,
            DIR_S_LONG to Direction.SOUTH,
            DIR_W to Direction.WEST,
            DIR_W_LONG to Direction.WEST,
            DIR_E to Direction.EAST,
            DIR_E_LONG to Direction.EAST
        )
    }

}

interface ProcessorWalk : SingleStateProcessor {
    companion object {
        const val COMMAND = "walk"
    }
}

val processorWalkModule = Kodein.Module("processorWalkModule") {
    bind<CommandRef>(COMMAND) with provider {
        CommandRef(
            name = COMMAND,
            processor = { commandMachine ->
                ProcessorWalkImpl(
                    commandMachine = commandMachine,
                    processorLook = instance<CommandRef>(ProcessorLook.COMMAND).processor(commandMachine),
                    playerManager = instance(),
                    worldManager = instance()
                )
            }
        )
    }
}
