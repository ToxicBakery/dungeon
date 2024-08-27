package com.toxicbakery.game.dungeon.machine.command

import com.toxicbakery.game.dungeon.exception.EmptyInputException
import com.toxicbakery.game.dungeon.exception.UnknownCommandException
import com.toxicbakery.game.dungeon.machine.Machine
import com.toxicbakery.game.dungeon.machine.command.processor.CommandGroupRef
import com.toxicbakery.game.dungeon.machine.command.processor.CommandRef
import com.toxicbakery.game.dungeon.machine.command.processor.commandGroupsModule
import com.toxicbakery.game.dungeon.machine.command.processor.commandProcessorFactoryModule
import com.toxicbakery.game.dungeon.manager.PlayerManager
import com.toxicbakery.game.dungeon.model.Lookable.Player
import com.toxicbakery.game.dungeon.model.session.GameSession
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.new
import org.kodein.di.provider

private class CommandMapImpl(
    private val commandGroupRefSet: Set<CommandGroupRef>,
    private val playerManager: PlayerManager,
) : CommandMap {

    private val commandRefMap: Map<String, CommandRef> by lazy {
        commandGroupRefSet.flatMap { group -> group.commands }
            .filterNot { command -> command.isPrivileged }
            .associateBy { command -> command.name }
    }

    private val commandRefAdminMap: Map<String, CommandRef> by lazy {
        commandRefMap + commandGroupRefSet.flatMap { group -> group.commands }
            .filter { command -> command.isPrivileged }
            .associateBy { command -> command.name }
    }

    override fun helpMessage(player: Player): String = commandGroupRefSet
        .mapNotNull { commandGroupRef ->
            val commands = commandGroupRef.commands
                .mapIndexedNotNull { index: Int, commandRef: CommandRef ->
                    if (commandRef.isPrivileged && !player.isAdmin) null
                    else when {
                        index == 0 -> commandRef.name
                        index % COMMANDS_PER_ROW == 0 -> "\n${commandRef.name}"
                        else -> ", ${commandRef.name}"
                    }
                }

            if (commands.isEmpty()) null
            else commandGroupRef to commands
        }.joinToString(separator = "\n\n") { (commandGroupRef, commands) ->
            "~~ ${commandGroupRef.name} ~~\n$commands"
        }

    @Suppress("IfThenToElvis")
    override suspend fun process(
        commandMachine: CommandMachine,
        gameSession: GameSession,
        message: String
    ): Machine<*> {
        val player = playerManager.getPlayerByGameSession(gameSession)
        val (command, args) = message.split(" ", limit = 2)
            .let { inputs ->
                if (inputs.isEmpty()) throw EmptyInputException()
                // returns "command" to "arg1 arg2 etc.."
                inputs.first() to (inputs.drop(1).firstOrNull() ?: "")
            }

        val commandMap = if (player.isAdmin) commandRefAdminMap else commandRefMap
        return commandMap[command].let { commandRef ->
            if (commandRef == null) throw UnknownCommandException(command)
            else commandRef.processor(commandMachine)
                .acceptMessage(gameSession, args)
        }
    }

    companion object {
        private const val COMMANDS_PER_ROW = 4
    }
}

interface CommandMap {

    /**
     * Create a help output for the user
     *
     * ```
     * ~~ Group Title ~~
     * command1, command2, command3
     * command4, command5
     * ```
     */
    fun helpMessage(player: Player): String

    /**
     * Process a user message automatically finding the command processor or throwing an exception
     */
    suspend fun process(
        commandMachine: CommandMachine,
        gameSession: GameSession,
        message: String
    ): Machine<*>
}

val commandMapModule = DI.Module("commandMapModule") {
    import(commandProcessorFactoryModule)
    import(commandGroupsModule)
    bind<CommandMap>() with provider { new(::CommandMapImpl) }
}
