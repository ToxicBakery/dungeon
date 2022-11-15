package com.toxicbakery.game.dungeon

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val storeDispatcher: CoroutineDispatcher = Dispatchers.Default
actual val tickDispatcher: CoroutineDispatcher = Dispatchers.Default
actual val gameProcessingDispatcher: CoroutineDispatcher = Dispatchers.Default
