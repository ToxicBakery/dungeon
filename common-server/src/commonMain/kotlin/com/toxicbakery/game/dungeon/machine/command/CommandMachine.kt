package com.toxicbakery.game.dungeon.machine.command

import com.toxicbakery.game.dungeon.exception.UnknownCommandException
import com.toxicbakery.game.dungeon.machine.Machine
import com.toxicbakery.game.dungeon.machine.ProcessorMachine
import com.toxicbakery.game.dungeon.manager.PlayerDataManager
import com.toxicbakery.game.dungeon.manager.PlayerManager
import com.toxicbakery.game.dungeon.model.Lookable.Player
import com.toxicbakery.game.dungeon.model.client.ClientMessage.PlayerDataMessage
import com.toxicbakery.game.dungeon.model.session.GameSession
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

private data class CommandMachineImpl(
    private val commandMap: CommandMap,
    private val playerDataManager: PlayerDataManager,
    private val playerManager: PlayerManager,
    override val currentState: CommandState = CommandState.Init
) : CommandMachine {

    override val name: String = "CommandMachine"

    override suspend fun initMachine(gameSession: GameSession): ProcessorMachine<CommandState> {
        if (currentState == CommandState.Init) gameSession.initCommand()
        return newInstance()
    }

    override suspend fun acceptMessage(
        gameSession: GameSession,
        message: String
    ): ProcessorMachine<*> {
        val player = playerManager.getPlayerByGameSession(gameSession)
        when (message) {
            COMMAND_HELP -> gameSession.helpCommand(player)
            else -> gameSession.handleSubCommand(message)
        }
        return this
    }

    private suspend fun GameSession.handleSubCommand(message: String): Machine<*> = try {
        commandMap.process(newInstance(), this, message)
    } catch (_: UnknownCommandException) {
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

    private suspend fun GameSession.helpCommand(player: Player) {
        sendMessage(commandMap.helpMessage(player))
        initCommand()
    }

    private fun newInstance(): CommandMachine = copy(currentState = CommandState.Initialized)

    companion object {
        private const val COMMAND_HELP = "help"
    }
}

interface CommandMachine : ProcessorMachine<CommandState>

val commandMachineModule = DI.Module("") {
    import(commandMapModule)
    bind<CommandMachine>() with singleton {
        CommandMachineImpl(
            commandMap = instance(),
            playerDataManager = instance(),
            playerManager = instance()
        )
    }
}
