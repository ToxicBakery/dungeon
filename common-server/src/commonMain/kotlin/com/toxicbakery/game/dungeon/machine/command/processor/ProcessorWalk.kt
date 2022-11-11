package com.toxicbakery.game.dungeon.machine.command.processor

import com.toxicbakery.game.dungeon.exception.UnknownCommandException
import com.toxicbakery.game.dungeon.machine.Machine
import com.toxicbakery.game.dungeon.machine.ProcessorMachine
import com.toxicbakery.game.dungeon.machine.command.CommandMachine
import com.toxicbakery.game.dungeon.machine.command.processor.Direction.directionMap
import com.toxicbakery.game.dungeon.machine.command.processor.ProcessorWalk.Companion.COMMAND
import com.toxicbakery.game.dungeon.manager.CommunicationManager
import com.toxicbakery.game.dungeon.manager.PlayerManager
import com.toxicbakery.game.dungeon.manager.WorldManager
import com.toxicbakery.game.dungeon.map.MapLegend
import com.toxicbakery.game.dungeon.map.model.Direction
import com.toxicbakery.game.dungeon.model.Lookable.Player
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.model.world.Location
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private class ProcessorWalkImpl(
    private val commandMachine: CommandMachine,
    private val processorLook: CommandProcessor<*>,
    private val playerManager: PlayerManager,
    private val worldManager: WorldManager,
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
        val lookLocation = worldManager.look(player, direction)
        val targetLocationMapLegend = MapLegend.representingByte(lookLocation.mapLegendByte)
        return if (walkableMapLegends.contains(targetLocationMapLegend)) {
            notifyPlayersAtLocation(
                message = "${player.name} departs to the ${direction.name.lowercase()}",
                location = player.location,
                sourcePlayer = player,
            )

            playerManager.updatePlayer(
                player.copy(location = lookLocation.location),
                gameSession
            )

            notifyPlayersAtLocation(
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

    private suspend fun notifyPlayersAtLocation(
        message: String,
        location: Location,
        sourcePlayer: Player,
    ) = playerManager.getPlayersAt(location)
        .asSequence()
        .filter { p -> p.id != sourcePlayer.id }
        .forEach { playerAtLocation ->
            communicationManager.notify(
                player = playerAtLocation,
                message = message
            )
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

val processorWalkModule = Kodein.Module("processorWalkModule") {
    bind<CommandRef>(COMMAND) with provider {
        CommandRef(
            name = COMMAND,
            processor = { commandMachine ->
                ProcessorWalkImpl(
                    commandMachine = commandMachine,
                    processorLook = instance<CommandRef>(ProcessorLook.COMMAND).processor(commandMachine),
                    playerManager = instance(),
                    worldManager = instance(),
                    communicationManager = instance(),
                )
            }
        )
    }
}
