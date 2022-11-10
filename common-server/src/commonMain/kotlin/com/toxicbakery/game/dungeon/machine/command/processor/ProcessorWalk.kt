package com.toxicbakery.game.dungeon.machine.command.processor

import com.toxicbakery.game.dungeon.exception.UnknownCommandException
import com.toxicbakery.game.dungeon.machine.Machine
import com.toxicbakery.game.dungeon.machine.command.CommandMachine
import com.toxicbakery.game.dungeon.machine.command.processor.Direction.directionMap
import com.toxicbakery.game.dungeon.machine.command.processor.ProcessorWalk.Companion.COMMAND
import com.toxicbakery.game.dungeon.manager.PlayerManager
import com.toxicbakery.game.dungeon.manager.WorldManager
import com.toxicbakery.game.dungeon.map.model.Direction
import com.toxicbakery.game.dungeon.model.session.GameSession
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private class ProcessorWalkImpl(
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
        ?: throw UnknownCommandException(name, message)

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
        return processorLook.acceptMessage(gameSession, "")
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
                    processorLook = instance<CommandRef>(ProcessorLook.COMMAND).processor(commandMachine),
                    playerManager = instance(),
                    worldManager = instance()
                )
            }
        )
    }
}
