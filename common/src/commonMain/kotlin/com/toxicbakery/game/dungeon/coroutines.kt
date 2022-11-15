package com.toxicbakery.game.dungeon

import kotlinx.coroutines.CoroutineDispatcher

expect val storeDispatcher: CoroutineDispatcher
expect val tickDispatcher: CoroutineDispatcher
expect val gameProcessingDispatcher: CoroutineDispatcher
