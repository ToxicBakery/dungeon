package com.toxicbakery.game.dungeon.machine.command

import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import com.toxicbakery.game.dungeon.machine.Machine
import com.toxicbakery.game.dungeon.manager.PlayerDataManager
import com.toxicbakery.game.dungeon.model.session.GameSession
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.factory
import org.kodein.di.erased.instance

private class CommandMachineImpl(
    private val gameSession: GameSession,
    private val playerDataManager: PlayerDataManager
) : CommandMachine {

    private val _currentState: AtomicReference<CommandState> = AtomicReference(CommandState.Init)

    override val name: String = "CommandMachine"

    override val currentState: CommandState
        get() = _currentState.value

    override suspend fun acceptMessage(message: String): Machine<*> {
        _currentState.value = cycle(message)
        return when (currentState) {
            CommandState.Init -> this
        }
    }

    override suspend fun initMachine() {
        _currentState.value = initCommand()
    }

    private suspend fun cycle(message: String): CommandState = when (message.toLowerCase()) {
        COMMAND_HELP -> helpCommand()
        else -> initCommand()
    }

    private suspend fun initCommand(): CommandState {
        gameSession.sendPlayerData(playerDataManager.getPlayerData(gameSession))
        return CommandState.Init
    }

    private suspend fun helpCommand(): CommandState {
        groupList
            .map { group -> "~~ ${group.name} ~~\n${group.commands.joinToString()}" }
            .joinToString(separator = "\n\n")
            .let { output -> gameSession.sendMessage(output) }
        return initCommand()
    }

    private data class CommandGroup(
        val name: String,
        val commands: List<String>
    )

    companion object {
        private const val COMMAND_HELP = "help"
        private const val COMMAND_LOOK = "look"
        private const val COMMAND_WALK = "walk"
        private const val COMMAND_GSAY = "gsay"
        private const val COMMAND_SAY = "say"
        private const val COMMAND_WHO = "who"

        private val movementGroup = CommandGroup(
            name = "Movement",
            commands = listOf(
                COMMAND_LOOK,
                COMMAND_WALK
            )
        )

        private val infoGroup = CommandGroup(
            name = "Info",
            commands = listOf(
                COMMAND_HELP
            )
        )

        private val chatGroup = CommandGroup(
            name = "Communication",
            commands = listOf(
                COMMAND_GSAY,
                COMMAND_SAY,
                COMMAND_WHO
            )
        )

        private val groupList: List<CommandGroup> = listOf(
            movementGroup,
            infoGroup,
            chatGroup
        )
    }

}

interface CommandMachine : Machine<CommandState>

val commandMachineModule = Kodein.Module("") {
    bind<CommandMachine>() with factory { gameSession: GameSession ->
        CommandMachineImpl(
            gameSession = gameSession,
            playerDataManager = instance()
        )
    }
}
