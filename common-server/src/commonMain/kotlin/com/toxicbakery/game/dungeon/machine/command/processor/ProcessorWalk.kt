package com.toxicbakery.game.dungeon.machine.command.processor

import com.toxicbakery.game.dungeon.machine.command.processor.ProcessorWalk.Companion.COMMAND
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.provider

private class ProcessorWalkImpl : ASingleStateProcessor(), ProcessorWalk {

    override val name: String = COMMAND

}

interface ProcessorWalk : SingleStateProcessor {
    companion object {
        const val COMMAND = "walk"
    }
}

val processorWalkModule = Kodein.Module("processorWalkModule") {
    bind<CommandRef>(COMMAND) with provider {
        CommandRef(
            name = COMMAND,
            processor = { _ ->
                ProcessorWalkImpl()
            }
        )
    }
}
