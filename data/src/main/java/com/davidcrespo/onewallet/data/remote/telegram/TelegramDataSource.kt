package com.davidcrespo.onewallet.data.remote.telegram

class TelegramDataSource(private val telegramApiClient: TelegramApiClient) {

    suspend fun sendMessage(message: String) {
        return telegramApiClient.sendMessage(message)
    }
}