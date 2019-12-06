package com.toxicbakery.game.dungeon.machine.command.processor

import com.toxicbakery.game.dungeon.machine.Machine
import com.toxicbakery.game.dungeon.machine.command.CommandMachine
import com.toxicbakery.game.dungeon.machine.command.processor.ProcessorLook.Companion.COMMAND
import com.toxicbakery.game.dungeon.manager.WorldManager
import com.toxicbakery.game.dungeon.model.client.ClientMessage.*
import com.toxicbakery.game.dungeon.model.session.GameSession
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private class ProcessorLookImpl(
    private val commandMachine: CommandMachine,
    private val worldManager: WorldManager
) : ASingleStateProcessor(), ProcessorLook {

    override val name: String = COMMAND

    override suspend fun acceptMessage(
        gameSession: GameSession,
        message: String
    ): Machine<*> {
        val map = worldManager.getWindow(gameSession)
        gameSession.sendClientMessage(MapMessage(map))
        return commandMachine
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
                    worldManager = instance()
                )
            }
        )
    }
}
