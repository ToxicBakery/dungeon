package com.toxicbakery.game.dungeon.machine.command.processor

import com.toxicbakery.game.dungeon.machine.command.processor.ProcessorLook.Companion.COMMAND
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.provider

private class ProcessorLookImpl : ASingleStateProcessor(), ProcessorLook {

    override val name: String = COMMAND

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
            processor = { _ ->
                ProcessorLookImpl()
            }
        )
    }
}
