package com.toxicbakery.game.dungeon.machine.command.processor

import com.toxicbakery.game.dungeon.exception.UnknownCommandException
import com.toxicbakery.game.dungeon.machine.ProcessorMachine
import com.toxicbakery.game.dungeon.machine.command.CommandMachine
import com.toxicbakery.game.dungeon.machine.command.processor.Direction.directionMap
import com.toxicbakery.game.dungeon.machine.command.processor.ProcessorWalk.Companion.COMMAND
import com.toxicbakery.game.dungeon.manager.CommunicationManager
import com.toxicbakery.game.dungeon.manager.LookManager
import com.toxicbakery.game.dungeon.manager.PlayerManager
import com.toxicbakery.game.dungeon.map.MapLegend
import com.toxicbakery.game.dungeon.map.model.Direction
import com.toxicbakery.game.dungeon.model.session.GameSession
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

private class ProcessorWalkImpl(
    private val commandMachine: CommandMachine,
    private val lookManager: LookManager,
    private val playerManager: PlayerManager,
    private val processorLook: CommandProcessor<*>,
    private val communicationManager: CommunicationManager,
) : ASingleStateProcessor(), ProcessorWalk {

    override val name: String = COMMAND

    override suspend fun acceptMessage(
        gameSession: GameSession,
        message: String
    ): ProcessorMachine<*> = directionMap[message]
        ?.let { direction -> walkDirection(gameSession, direction) }
        ?: throw UnknownCommandException(name, message)

    private suspend fun walkDirection(
        gameSession: GameSession,
        direction: Direction
    ): ProcessorMachine<*> {
        val player = playerManager.getPlayerByGameSession(gameSession)
        val lookLocation = lookManager.look(player, direction)
        val targetLocationMapLegend = MapLegend.representingByte(lookLocation.mapLegendByte)
        return if (walkableMapLegends.contains(targetLocationMapLegend)) {
            communicationManager.notifyPlayersAtLocation(
                message = "${player.name} departs to the ${direction.name.lowercase()}",
                location = player.location,
                sourcePlayer = player,
            )

            playerManager.updatePlayer(
                player.copy(location = lookLocation.location),
            )

            communicationManager.notifyPlayersAtLocation(
                message = "${player.name} arrives from ${direction.sourceDirection.name.lowercase()}",
                location = lookLocation.location,
                sourcePlayer = player,
            )

            // Redisplay the map
            processorLook.acceptMessage(gameSession, "")
        } else {
            gameSession.sendMessage("You can't swim that far!")
            commandMachine
        }
    }

    companion object {
        private val walkableMapLegends = setOf(
            MapLegend.FOREST_1,
            MapLegend.FOREST_2,
            MapLegend.FOREST_3,
            MapLegend.FOREST_4,
            MapLegend.DESERT,
            MapLegend.PLAIN,
            MapLegend.BEACH,
            MapLegend.MOUNTAIN,
        )
    }
}

interface ProcessorWalk : SingleStateProcessor {
    companion object {
        const val COMMAND = "walk"
    }
}

val processorWalkModule = DI.Module("processorWalkModule") {
    bind<CommandRef>(COMMAND) with provider {
        CommandRef(
            name = COMMAND,
            processor = { commandMachine ->
                ProcessorWalkImpl(
                    commandMachine = commandMachine,
                    communicationManager = instance(),
                    lookManager = instance(),
                    processorLook = instance<CommandRef>(ProcessorLook.COMMAND).processor(commandMachine),
                    playerManager = instance(),
                )
            }
        )
    }
}
