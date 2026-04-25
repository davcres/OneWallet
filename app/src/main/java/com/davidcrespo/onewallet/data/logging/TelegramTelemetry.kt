package com.davidcrespo.onewallet.data.logging

import android.util.Log
import com.davidcrespo.onewallet.data.remote.telegram.TelegramDataSource
import com.davidcrespo.onewallet.domain.logging.Telemetry

class TelegramTelemetry(private val telegramDataSource: TelegramDataSource) : Telemetry {

    override suspend fun log(message: String) {
        Log.e("***", message)
        runCatching { telegramDataSource.sendMessage(message) }
    }
}