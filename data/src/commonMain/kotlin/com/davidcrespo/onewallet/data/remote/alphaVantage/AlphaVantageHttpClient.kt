package com.davidcrespo.onewallet.data.remote.alphaVantage

import com.davidcrespo.onewallet.data.remote.DeviceIdProvider
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
    private val deviceIdProvider: DeviceIdProvider
) {

    suspend inline fun <reified T> get(
        path: String,
        noinline block: HttpRequestBuilder.() -> Unit
    ): T {
        val keyIndex = abs(getDeviceSeed()) % apiKeys.size
        val key = apiKeys[keyIndex]

        val body = executeRequest(path, key, block)

        val isRateLimited =
            body.contains("alphavantage.co/premium", ignoreCase = true)

        if (isRateLimited) {
            error("All Alpha Vantage API keys exhausted")
        }

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
    fun getDeviceSeed(): Int {
        return deviceIdProvider.getDeviceId().hashCode()
    }
}