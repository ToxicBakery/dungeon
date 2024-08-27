package com.toxicbakery.game.dungeon.machine.command.processor

import org.kodein.di.DI

val commandProcessorFactoryModule = DI.Module("commandProcessorFactoryModule") {
    import(processorGsayModule)
    import(processorLookModule)
    import(processorSayModule)
    import(processorShoutModule)
    import(processorSpawnModule)
    import(processorWalkModule)
    import(processorWhoModule)
}
