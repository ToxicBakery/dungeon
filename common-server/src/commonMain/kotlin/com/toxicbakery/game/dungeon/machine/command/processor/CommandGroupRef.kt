package com.toxicbakery.game.dungeon.machine.command.processor

data class CommandGroupRef(
    val name: String,
    val commands: List<CommandRef>
)
