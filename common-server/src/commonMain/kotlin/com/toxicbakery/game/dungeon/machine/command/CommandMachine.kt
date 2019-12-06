package com.toxicbakery.game.dungeon.machine.command

import com.toxicbakery.game.dungeon.exception.UnknownCommandException
import com.toxicbakery.game.dungeon.machine.Machine
import com.toxicbakery.game.dungeon.manager.PlayerDataManager
import com.toxicbakery.game.dungeon.model.client.ClientMessage.PlayerDataMessage
import com.toxicbakery.game.dungeon.model.session.GameSession
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton

private class CommandMachineImpl(
    private val commandMap: CommandMap,
    private val playerDataManager: PlayerDataManager,
    override val currentState: CommandState = CommandState.Init
) : CommandMachine {

    override val name: String = "CommandMachine"

    override suspend fun initMachine(gameSession: GameSession): Machine<CommandState> {
        if (currentState == CommandState.Init) gameSession.initCommand()
        return newInstance()
    }

    override suspend fun acceptMessage(
        gameSession: GameSession,
        message: String
    ): Machine<*> {
        when (message) {
            COMMAND_HELP -> gameSession.helpCommand()
            else -> gameSession.handleSubCommand(message)
        }
        return this
    }

    private suspend fun GameSession.handleSubCommand(message: String): Machine<*> = try {
        commandMap.process(newInstance(), this, message)
    } catch (e: UnknownCommandException) {
        processorFailure()
        this@CommandMachineImpl
    } finally {
        initCommand()
    }

    private suspend fun GameSession.processorFailure() {
        sendMessage("Huh?")
        initCommand()
    }

    private suspend fun GameSession.initCommand() {
        val playerData = playerDataManager.getPlayerData(this)
        sendClientMessage(PlayerDataMessage(playerData))
    }

    private suspend fun GameSession.helpCommand() {
        sendMessage(commandMap.helpMessage)
        initCommand()
    }

    private fun newInstance(): CommandMachine = CommandMachineImpl(
        commandMap = commandMap,
        playerDataManager = playerDataManager,
        currentState = CommandState.Initialized
    )

    companion object {
        private const val COMMAND_HELP = "help"
    }

}

interface CommandMachine : Machine<CommandState>

val commandMachineModule = Kodein.Module("") {
    import(commandMapModule)
    bind<CommandMachine>() with singleton {
        CommandMachineImpl(
            commandMap = instance(),
            playerDataManager = instance()
        )
    }
}
