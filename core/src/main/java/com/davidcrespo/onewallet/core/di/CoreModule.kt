package com.davidcrespo.onewallet.core.di

import android.content.Context
import com.davidcrespo.onewallet.domain.di.AppCoroutineScope
import com.davidcrespo.onewallet.domain.di.DispatcherProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {
    single {
        androidContext().getSharedPreferences("onewallet_prefs", Context.MODE_PRIVATE)
    }

    single<DispatcherProvider> { DispatcherProviderImpl() }
    single { AppCoroutineScope(get()) }
}
