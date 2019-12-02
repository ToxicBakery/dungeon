package com.toxicbakery.game.dungeon.machine.command.processor

interface CommandProcessor {

    /**
     * The command that triggers the processor.
     */
    val name: String

    /**
     * Hook for processing arguments received for the command.
     */
    suspend fun process(arguments: String)

}
