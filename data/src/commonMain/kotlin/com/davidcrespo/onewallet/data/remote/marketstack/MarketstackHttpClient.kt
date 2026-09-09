package com.davidcrespo.onewallet.data.remote.marketstack

import com.davidcrespo.onewallet.data.remote.DeviceIdProvider
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
    private val deviceIdProvider: DeviceIdProvider
) {

    suspend inline fun <reified T> get(
        path: String,
        noinline block: HttpRequestBuilder.() -> Unit
    ): T {
        val keyIndex = abs(getDeviceSeed()) % apiKeys.size
        val key = apiKeys[keyIndex]

        val response = executeRequest(path, key, block)

        handleErrors(response)

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
                error("Rate limit exceeded (429)")
            }

            else -> {
                if (isUsageLimitReached(body)) {
                    error("Monthly usage limit reached")
                }

                if (!response.status.isSuccess()) {
                    error("Marketstack error: ${response.status}")
                }
            }
        }
    }

    fun isUsageLimitReached(body: String): Boolean {
        return body.contains("usage_limit_reached", ignoreCase = true)
    }

    fun getDeviceSeed(): Int {
        return deviceIdProvider.getDeviceId().hashCode()
    }
}