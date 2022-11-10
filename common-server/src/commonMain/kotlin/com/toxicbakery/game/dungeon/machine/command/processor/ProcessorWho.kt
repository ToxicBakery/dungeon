package com.toxicbakery.game.dungeon.machine.command.processor

import com.toxicbakery.game.dungeon.machine.Machine
import com.toxicbakery.game.dungeon.machine.command.CommandMachine
import com.toxicbakery.game.dungeon.machine.command.processor.ProcessorWho.Companion.COMMAND
import com.toxicbakery.game.dungeon.manager.CommunicationManager
import com.toxicbakery.game.dungeon.model.session.GameSession
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private class ProcessorWhoImpl(
    private val commandMachine: CommandMachine,
    private val communicationManager: CommunicationManager
) : ASingleStateProcessor(), ProcessorWho {

    override val name: String = COMMAND

    override suspend fun acceptMessage(
        gameSession: GameSession,
        message: String
    ): Machine<*> {
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

val processorWhoModule = Kodein.Module("processorWhoModule") {
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
