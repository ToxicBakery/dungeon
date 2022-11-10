package com.toxicbakery.game.dungeon.machine.command.processor

import com.toxicbakery.game.dungeon.exception.UnknownCommandException
import com.toxicbakery.game.dungeon.machine.Machine
import com.toxicbakery.game.dungeon.machine.command.CommandMachine
import com.toxicbakery.game.dungeon.machine.command.processor.Direction.directionMap
import com.toxicbakery.game.dungeon.machine.command.processor.ProcessorLook.Companion.COMMAND
import com.toxicbakery.game.dungeon.manager.PlayerManager
import com.toxicbakery.game.dungeon.manager.WorldManager
import com.toxicbakery.game.dungeon.map.model.Direction
import com.toxicbakery.game.dungeon.model.client.ClientMessage.DirectedLookMessage
import com.toxicbakery.game.dungeon.model.client.ClientMessage.MapMessage
import com.toxicbakery.game.dungeon.model.session.GameSession
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private class ProcessorLookImpl(
    private val commandMachine: CommandMachine,
    private val playerManager: PlayerManager,
    private val worldManager: WorldManager
) : ASingleStateProcessor(), ProcessorLook {

    override val name: String = COMMAND

    override suspend fun acceptMessage(
        gameSession: GameSession,
        message: String
    ): Machine<*> {
        if (message.isEmpty()) {
            val window = worldManager.getWindow(gameSession)
            gameSession.sendClientMessage(MapMessage(window))
            lookDirection(gameSession)
        } else {
            directionMap[message]
                ?.let { direction -> lookDirection(gameSession, direction) }
                ?: throw UnknownCommandException(name, message)
        }
        return commandMachine
    }

    private suspend fun lookDirection(
        gameSession: GameSession,
        direction: Direction? = null
    ) {
        val player = playerManager.getPlayerByGameSession(gameSession)
        val lookLocation = worldManager.look(player, direction)
        gameSession.sendClientMessage(DirectedLookMessage(lookLocation))
    }
}

interface ProcessorLook : SingleStateProcessor {
    companion object {
        const val COMMAND = "look"
    }
}

val processorLookModule = Kodein.Module("processorLookModule") {
    bind<CommandRef>(COMMAND) with provider {
        CommandRef(
            name = COMMAND,
            processor = { commandMachine ->
                ProcessorLookImpl(
                    commandMachine = commandMachine,
                    worldManager = instance(),
                    playerManager = instance(),
                )
            }
        )
    }
}
