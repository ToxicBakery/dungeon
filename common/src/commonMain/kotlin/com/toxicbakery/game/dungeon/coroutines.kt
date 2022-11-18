package com.toxicbakery.game.dungeon

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

internal expect val storeDispatcher: CoroutineDispatcher
internal expect val tickDispatcher: CoroutineDispatcher
internal expect val gameProcessingDispatcher: CoroutineDispatcher
expect val tickScope: CoroutineScope
expect val storeScope: CoroutineScope
expect val gameProcessingScope: CoroutineScope
