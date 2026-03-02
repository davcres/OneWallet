package com.davidcrespo.onewallet.di

import android.content.Context
import com.davidcrespo.onewallet.BuildConfig
import com.davidcrespo.onewallet.domain.di.DispatcherProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {

    single {
        androidContext().getSharedPreferences("onewallet_prefs", Context.MODE_PRIVATE)
    }

    // Keys (keep BuildConfig in DI layer)
    single(FINNHUB_KEY) { BuildConfig.FINNHUB_API_KEY }
    single(ALPHA_VANTAGE_KEY) { BuildConfig.ALPHA_VANTAGE_API_KEY }
    single(ALPHA_VANTAGE_KEY_2) { BuildConfig.ALPHA_VANTAGE_API_KEY_2 }
    single(ALPHA_VANTAGE_KEY_3) { BuildConfig.ALPHA_VANTAGE_API_KEY_3 }
    single(TWELVE_DATA_KEY) { BuildConfig.TWELVE_DATA_API_KEY }
    single(TELEGRAM_API_KEY) { BuildConfig.TELEGRAM_API_KEY }
    single(TELEGRAM_CHAT_ID) { BuildConfig.TELEGRAM_CHAT_ID }

    single<DispatcherProvider> { DispatcherProviderImpl() }
}
