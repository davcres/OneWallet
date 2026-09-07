package com.davidcrespo.onewallet.data.di

import com.davidcrespo.onewallet.data.BuildConfig
import com.davidcrespo.onewallet.data.logging.NoOpTelemetry
import com.davidcrespo.onewallet.data.logging.TelegramTelemetry
import com.davidcrespo.onewallet.domain.cache.CachePolicy
import com.davidcrespo.onewallet.domain.logging.Telemetry
import org.koin.dsl.module

val policyModule = module {

    single(TELEGRAM_API_KEY) { BuildConfig.TELEGRAM_API_KEY }
    single(TELEGRAM_CHAT_ID) { BuildConfig.TELEGRAM_CHAT_ID }

    // TTLs distintos por tipo
    single {
        if (BuildConfig.DEBUG) {
            CachePolicy(
                stockHours = 24 * 7,
                cryptoHours = 1,
                fundHours = 2,
                etfHours = 2,
                marketHours = 24 * 365,
                rateHours = 24 * 365
            )
        } else {
            CachePolicy(
                stockHours = 1,
                cryptoHours = 1,
                fundHours = 1,
                etfHours = 1,
                marketHours = 24 * 7,
                rateHours = 24
            )
        }
    }

    // Telemetry: debug -> Telegram, release -> NoOp
    single<Telemetry> {
        if (BuildConfig.DEBUG) TelegramTelemetry(get())
        else NoOpTelemetry
    }
}