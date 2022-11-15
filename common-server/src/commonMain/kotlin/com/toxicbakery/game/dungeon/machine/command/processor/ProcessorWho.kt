package com.toxicbakery.game.dungeon.machine.command.processor

import com.toxicbakery.game.dungeon.machine.ProcessorMachine
import com.toxicbakery.game.dungeon.machine.command.CommandMachine
import com.toxicbakery.game.dungeon.machine.command.processor.ProcessorWho.Companion.COMMAND
import com.toxicbakery.game.dungeon.manager.CommunicationManager
import com.toxicbakery.game.dungeon.model.session.GameSession
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

private class ProcessorWhoImpl(
    private val commandMachine: CommandMachine,
    private val communicationManager: CommunicationManager
) : ASingleStateProcessor(), ProcessorWho {

    override val name: String = COMMAND

    override suspend fun acceptMessage(
        gameSession: GameSession,
        message: String
    ): ProcessorMachine<*> {
        communicationManager.who()
            .joinToString(separator = "\n") { player -> player.name }
            .let { output -> gameSession.sendMessage("Players Online:\n$output") }

        return commandMachine
    }
}

interface ProcessorWho : SingleStateProcessor {
    companion object {
        const val COMMAND = "who"
    }
}

val processorWhoModule = DI.Module("processorWhoModule") {
    bind<CommandRef>(COMMAND) with provider {
        CommandRef(
            name = COMMAND,
            processor = { commandMachine ->
                ProcessorWhoImpl(
                    commandMachine = commandMachine,
                    communicationManager = instance()
                )
            }
        )
    }
}
