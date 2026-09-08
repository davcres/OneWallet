package com.davidcrespo.onewallet.core.di

import com.davidcrespo.onewallet.domain.di.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

expect val ioDispatcher: CoroutineDispatcher

class DispatcherProviderImpl : DispatcherProvider {
    override val io: CoroutineDispatcher = ioDispatcher
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val main: CoroutineDispatcher = Dispatchers.Main
}
