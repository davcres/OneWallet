package com.davidcrespo.onewallet.data.logging

import com.davidcrespo.onewallet.data.remote.telegram.TelegramDataSource
import com.davidcrespo.onewallet.domain.logging.Telemetry

class TelegramTelemetry(private val telegramDataSource: TelegramDataSource) : Telemetry {

    override suspend fun log(message: String) {
        runCatching { telegramDataSource.sendMessage(message) }
    }
}