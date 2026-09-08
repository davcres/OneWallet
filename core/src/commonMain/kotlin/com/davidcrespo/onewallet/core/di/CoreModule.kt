package com.davidcrespo.onewallet.core.di

import com.davidcrespo.onewallet.domain.di.AppCoroutineScope
import com.davidcrespo.onewallet.domain.di.DispatcherProvider
import org.koin.dsl.module

val coreModule = module {
    single<DispatcherProvider> { DispatcherProviderImpl() }
    single { AppCoroutineScope(get()) }
}
