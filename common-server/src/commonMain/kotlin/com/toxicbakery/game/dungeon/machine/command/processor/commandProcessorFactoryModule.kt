package com.toxicbakery.game.dungeon.machine.command.processor

import org.kodein.di.Kodein

val commandProcessorFactoryModule = Kodein.Module("commandProcessorFactoryModule") {
    import(processorGsayModule)
    import(processorLookModule)
    import(processorSayModule)
    import(processorShoutModule)
    import(processorWalkModule)
    import(processorWhoModule)
}
