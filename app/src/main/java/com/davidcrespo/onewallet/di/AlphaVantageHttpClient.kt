package com.davidcrespo.onewallet.di

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.davidcrespo.onewallet.data.remote.alphaVantage.AlphaVantageApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlin.math.abs

/**
 * Custom HTTP Client with API key rotation.
 */
class AlphaVantageHttpClient(
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

        val body = executeRequest(path, key, block)

        val isRateLimited =
            body.contains("alphavantage.co/premium", ignoreCase = true)

        if (isRateLimited) {
            Log.w("AlphaVantage", "Rate limit hit with device-assigned key index: $keyIndex")
            error("All Alpha Vantage API keys exhausted")
        }

        Log.d("AlphaVantage", "Request success with device-assigned key index: $keyIndex")
        return json.decodeFromString(body)
    }

    suspend fun executeRequest(
        path: String,
        key: String,
        block: HttpRequestBuilder.() -> Unit
    ): String {
        return client.get(path) {
            parameter(AlphaVantageApiConfig.TOKEN, key)
            block()
        }.bodyAsText()
    }

    /**
     * Para que cada dispositivo tenga una key diferente.
     * No vale iterarlas todas porque cuando una llega al limite tampoco deja usar las demás.
     */
    fun getDeviceSeed(context: Context): Int {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        return androidId.hashCode()
    }
}