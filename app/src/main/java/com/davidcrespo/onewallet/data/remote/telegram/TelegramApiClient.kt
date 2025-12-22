package com.davidcrespo.onewallet.data.remote.telegram

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class TelegramApiClient(private val client: HttpClient) {

    suspend fun sendMessage(message: String) {
        val token = "8575995942:AAF06D97HUQVwFL34mFARH_agGBrVid7LM0"
        val chatId = "425833641"
        val url = "https://api.telegram.org/bot$token/sendMessage?chat_id=$chatId&text=$message"

        return client.get(url).body()
    }
}
