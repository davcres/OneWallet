package com.davidcrespo.onewallet.di

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.davidcrespo.onewallet.data.remote.marketstack.MarketstackApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlin.math.abs

/**
 * Custom HTTP Client with API key rotation.
 */
class MarketstackHttpClient(
    val client: HttpClient,
    val json: Json,
    val apiKeys: List<String>,
    val context: Context
) {

    suspend inline fun <reified T> get(
        path: String,
        noinline block: HttpRequestBuilder.() -> Unit
    ): T {

        val keyIndex = abs(getDeviceSeed(context)) % apiKeys.size
        val key = apiKeys[keyIndex]

        val response = executeRequest(path, key, block)

        handleErrors(response)

        Log.d("Marketstack", "Request success with device-assigned key index: $keyIndex")

        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun executeRequest(
        path: String,
        key: String,
        block: HttpRequestBuilder.() -> Unit
    ): HttpResponse {
        return client.get(path) {
            parameter(MarketstackApiConfig.TOKEN, key)
            block()
        }
    }

    suspend fun handleErrors(response: HttpResponse) {

        val body = response.bodyAsText()

        when (response.status) {
            HttpStatusCode.TooManyRequests -> {
                Log.w("Marketstack", "HTTP 429 - Rate limit exceeded")
                error("Rate limit exceeded (429)")
            }

            else -> {
                if (isUsageLimitReached(body)) {
                    Log.w("Marketstack", "Monthly usage limit reached")
                    error("Monthly usage limit reached")
                }

                if (!response.status.isSuccess()) {
                    Log.e("Marketstack", "Error: ${response.status}")
                    error("Marketstack error: ${response.status}")
                }
            }
        }
    }

    fun isUsageLimitReached(body: String): Boolean {
        return body.contains("usage_limit_reached", ignoreCase = true)
    }

    fun getDeviceSeed(context: Context): Int {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        return androidId.hashCode()
    }
}