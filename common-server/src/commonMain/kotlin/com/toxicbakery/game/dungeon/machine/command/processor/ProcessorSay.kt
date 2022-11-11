package com.toxicbakery.game.dungeon.machine.command.processor

import com.toxicbakery.game.dungeon.machine.ProcessorMachine
import com.toxicbakery.game.dungeon.machine.command.CommandMachine
import com.toxicbakery.game.dungeon.machine.command.processor.ProcessorSay.Companion.COMMAND
import com.toxicbakery.game.dungeon.manager.CommunicationManager
import com.toxicbakery.game.dungeon.manager.PlayerManager
import com.toxicbakery.game.dungeon.model.Lookable.Player
import com.toxicbakery.game.dungeon.model.session.GameSession
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private class ProcessorSayImpl(
    private val commandMachine: CommandMachine,
    private val communicationManager: CommunicationManager,
    private val playerManager: PlayerManager
) : ASingleStateProcessor(), ProcessorSay {

    override val name: String = COMMAND

    override suspend fun acceptMessage(
        gameSession: GameSession,
        message: String
    ): ProcessorMachine<*> {
        val player: Player = playerManager.getPlayerByGameSession(gameSession)
        communicationManager.say(player, message)
        return commandMachine
    }
}

interface ProcessorSay : SingleStateProcessor {
    companion object {
        const val COMMAND = "say"
    }
}

val processorSayModule = Kodein.Module("processorSayModule") {
    bind<CommandRef>(COMMAND) with provider {
        CommandRef(
            name = COMMAND,
            processor = { commandMachine ->
                ProcessorSayImpl(
                    commandMachine = commandMachine,
                    communicationManager = instance(),
                    playerManager = instance()
                )
            }
        )
    }
}
