package com.toxicbakery.game.dungeon.machine.command.processor

import com.toxicbakery.game.dungeon.machine.command.CommandMachine

data class CommandRef(
    val name: String,
    val isPrivileged: Boolean = false,
    val processor: (CommandMachine) -> CommandProcessor<*>
)
