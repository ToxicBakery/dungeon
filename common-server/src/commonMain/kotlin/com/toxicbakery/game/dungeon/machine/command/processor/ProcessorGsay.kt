package com.toxicbakery.game.dungeon.machine.command.processor

import com.toxicbakery.game.dungeon.machine.ProcessorMachine
import com.toxicbakery.game.dungeon.machine.command.CommandMachine
import com.toxicbakery.game.dungeon.machine.command.processor.ProcessorGsay.Companion.COMMAND
import com.toxicbakery.game.dungeon.manager.CommunicationManager
import com.toxicbakery.game.dungeon.manager.PlayerManager
import com.toxicbakery.game.dungeon.model.session.GameSession
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

private class ProcessorGsayImpl(
    private val commandMachine: CommandMachine,
    private val communicationManager: CommunicationManager,
    private val playerManager: PlayerManager
) : ASingleStateProcessor(), ProcessorGsay {

    override val name: String = COMMAND

    override suspend fun acceptMessage(
        gameSession: GameSession,
        message: String
    ): ProcessorMachine<*> {
        val player = playerManager.getPlayerByGameSession(gameSession)
        communicationManager.gsay(player, message)
        return commandMachine
    }
}

interface ProcessorGsay : SingleStateProcessor {

    companion object {
        const val COMMAND = "gsay"
    }
}

val processorGsayModule = DI.Module("processorGsayModule") {
    bind<CommandRef>(COMMAND) with provider {
        CommandRef(
            name = COMMAND,
            processor = { commandMachine ->
                ProcessorGsayImpl(
                    commandMachine = commandMachine,
                    communicationManager = instance(),
                    playerManager = instance()
                )
            }
        )
    }
}
