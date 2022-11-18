package com.toxicbakery.game.dungeon

import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher

internal actual val storeDispatcher: CoroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
internal actual val tickDispatcher: CoroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
internal actual val gameProcessingDispatcher: CoroutineDispatcher =
    Executors.newSingleThreadExecutor().asCoroutineDispatcher()

actual val tickScope: CoroutineScope = CoroutineScope(tickDispatcher)
actual val storeScope: CoroutineScope = CoroutineScope(storeDispatcher)
actual val gameProcessingScope: CoroutineScope = CoroutineScope(gameProcessingDispatcher)