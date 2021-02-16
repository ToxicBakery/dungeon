package com.toxicbakery.game.dungeon.machine.command.processor

import com.toxicbakery.game.dungeon.machine.Machine
import com.toxicbakery.game.dungeon.machine.command.CommandMachine
import com.toxicbakery.game.dungeon.machine.command.processor.ProcessorShout.Companion.COMMAND
import com.toxicbakery.game.dungeon.manager.CommunicationManager
import com.toxicbakery.game.dungeon.manager.PlayerManager
import com.toxicbakery.game.dungeon.model.session.GameSession
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private class ProcessorShoutImpl(
    private val commandMachine: CommandMachine,
    private val communicationManager: CommunicationManager,
    private val playerManager: PlayerManager
) : ASingleStateProcessor(), ProcessorShout {

    override val name: String = COMMAND

    override suspend fun acceptMessage(
        gameSession: GameSession,
        message: String
    ): Machine<*> {
        val player = playerManager.getPlayerByGameSession(gameSession)
        communicationManager.shout(player, message)
        return commandMachine
    }

}

interface ProcessorShout : SingleStateProcessor {
    companion object {
        const val COMMAND = "shout"
    }
}

val processorShoutModule = Kodein.Module("processorShoutModule") {
    bind<CommandRef>(COMMAND) with provider {
        CommandRef(
            name = COMMAND,
            processor = { commandMachine ->
                ProcessorShoutImpl(
                    commandMachine = commandMachine,
                    communicationManager = instance(),
                    playerManager = instance()
                )
            }
        )
    }
}
