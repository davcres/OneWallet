package com.davidcrespo.onewallet.util

import com.davidcrespo.onewallet.domain.di.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

/**
 * A [DispatcherProvider] implementation to be used in unit tests.
 * All dispatchers return the same [testDispatcher] (default is [UnconfinedTestDispatcher]).
 */
class TestDispatcherProvider(
    private val testDispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()
) : DispatcherProvider {
    override val main: CoroutineDispatcher = testDispatcher
    override val io: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
}
