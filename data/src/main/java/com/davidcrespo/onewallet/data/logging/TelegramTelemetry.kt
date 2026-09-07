package com.davidcrespo.onewallet.data.logging

import android.os.Build
import android.util.Log
import com.davidcrespo.onewallet.data.remote.telegram.TelegramDataSource
import com.davidcrespo.onewallet.domain.logging.Telemetry

class TelegramTelemetry(private val telegramDataSource: TelegramDataSource) : Telemetry {

    override suspend fun log(message: String) {
        Log.e("***", "${Build.DEVICE}: $message")
        if (!Build.DEVICE.contains(EMULATOR, ignoreCase = true)) {
            runCatching { telegramDataSource.sendMessage(message) }
        }
    }

    companion object {
        const val EMULATOR = "emu64a"
    }
}