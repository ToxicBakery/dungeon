package com.toxicbakery.game.dungeon

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

internal actual val storeDispatcher: CoroutineDispatcher = Dispatchers.Default
internal actual val tickDispatcher: CoroutineDispatcher = Dispatchers.Default
internal actual val gameProcessingDispatcher: CoroutineDispatcher = Dispatchers.Default
actual val tickScope: CoroutineScope = CoroutineScope(tickDispatcher)
actual val storeScope: CoroutineScope = CoroutineScope(storeDispatcher)
actual val gameProcessingScope: CoroutineScope = CoroutineScope(gameProcessingDispatcher)
