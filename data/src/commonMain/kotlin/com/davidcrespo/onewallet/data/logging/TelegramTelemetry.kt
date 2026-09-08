package com.davidcrespo.onewallet.data.logging

import com.davidcrespo.onewallet.data.remote.telegram.TelegramDataSource
import com.davidcrespo.onewallet.domain.logging.Telemetry

class TelegramTelemetry(
    private val telegramDataSource: TelegramDataSource,
    private val deviceName: String = "Device"
) : Telemetry {

    override suspend fun log(message: String) {
        if (!deviceName.contains(EMULATOR, ignoreCase = true)) {
            runCatching { telegramDataSource.sendMessage("[$deviceName] $message") }
        }
    }

    companion object {
        const val EMULATOR = "emu64a"
    }
}