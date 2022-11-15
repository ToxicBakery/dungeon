package com.toxicbakery.game.dungeon.machine.command

import com.toxicbakery.game.dungeon.exception.EmptyInputException
import com.toxicbakery.game.dungeon.exception.UnknownCommandException
import com.toxicbakery.game.dungeon.machine.Machine
import com.toxicbakery.game.dungeon.machine.command.processor.CommandGroupRef
import com.toxicbakery.game.dungeon.machine.command.processor.CommandRef
import com.toxicbakery.game.dungeon.machine.command.processor.commandGroupsModule
import com.toxicbakery.game.dungeon.machine.command.processor.commandProcessorFactoryModule
import com.toxicbakery.game.dungeon.model.session.GameSession
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

private class CommandMapImpl(
    commandGroupRefSet: Set<CommandGroupRef>
) : CommandMap {

    private val commandRefMap: Map<String, CommandRef> by lazy {
        commandGroupRefSet.flatMap { group -> group.commands }
            .map { command -> command.name to command }
            .toMap()
    }

    override val helpMessage: String by lazy {
        commandGroupRefSet.joinToString(separator = "\n\n") { group ->
            val commands = group.commands
                .mapIndexed { index: Int, (command: String, _) ->
                    when {
                        index == 0 -> command
                        index % COMMANDS_PER_ROW == 0 -> "\n$command"
                        else -> ", $command"
                    }
                }
                .joinToString("")

            "~~ ${group.name} ~~\n$commands"
        }
    }

    @Suppress("IfThenToElvis")
    override suspend fun process(
        commandMachine: CommandMachine,
        gameSession: GameSession,
        message: String
    ): Machine<*> {
        val (command, args) = message.split(" ", limit = 2)
            .let { inputs ->
                if (inputs.isEmpty()) throw EmptyInputException()
                // returns "command" to "arg1 arg2 etc.."
                inputs.first() to (inputs.drop(1).firstOrNull() ?: "")
            }

        return commandRefMap[command].let { commandRef ->
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
    val helpMessage: String

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
    bind<CommandMap>() with provider {
        CommandMapImpl(
            commandGroupRefSet = instance()
        )
    }
}
