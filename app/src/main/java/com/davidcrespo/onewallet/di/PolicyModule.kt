package com.davidcrespo.onewallet.di

import com.davidcrespo.onewallet.BuildConfig
import com.davidcrespo.onewallet.data.logging.NoOpTelemetry
import com.davidcrespo.onewallet.data.logging.TelegramTelemetry
import com.davidcrespo.onewallet.domain.cache.CachePolicy
import com.davidcrespo.onewallet.domain.logging.Telemetry
import org.koin.dsl.module

val policyModule = module {

    // TTLs distintos por tipo
    single {
        if (BuildConfig.DEBUG) {
            CachePolicy(
                investmentHours = 24 * 7,
                marketHours = 24 * 7,
                rateHours = 24 * 7
            )
        } else {
            CachePolicy(
                investmentHours = 2,
                marketHours = 24,
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