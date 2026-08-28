package com.davidcrespo.onewallet.data.remote.telegram

import com.davidcrespo.onewallet.data.remote.telegram.models.SendMessageRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class TelegramApiClient(
    private val client: HttpClient,
    private val botToken: String,
    private val chatId: String
) {

    suspend fun sendMessage(message: String) {
        client.post("/bot$botToken/sendMessage") {
            contentType(ContentType.Application.Json)
            setBody(SendMessageRequest(chat_id = chatId, text = message))
        }
    }

}
