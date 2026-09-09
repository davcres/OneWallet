package com.davidcrespo.onewallet.data.di

import com.davidcrespo.onewallet.data.logging.NoOpTelemetry
import com.davidcrespo.onewallet.data.logging.TelegramTelemetry
import com.davidcrespo.onewallet.domain.cache.CachePolicy
import com.davidcrespo.onewallet.domain.logging.Telemetry
import org.koin.dsl.module

val policyModule = module {

    // TTLs distintos por tipo
    single {
        val isDebug = getOrNull<Boolean>(IS_DEBUG) ?: false
        if (isDebug) {
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
        val isDebug = getOrNull<Boolean>(IS_DEBUG) ?: false
        val deviceName = getOrNull<String>(DEVICE_NAME) ?: "Device"
        if (isDebug) TelegramTelemetry(get(), deviceName)
        else NoOpTelemetry
    }
}