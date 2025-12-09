package com.davidcrespo.onewallet.data.remote

import com.davidcrespo.onewallet.BuildConfig
import com.davidcrespo.onewallet.data.models.PriceResponse
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object ApiClient {

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.twelvedata.com"
            }
        }
    }

    suspend fun getPrice(symbol: String): PriceResponse {
        return client.get("price") {
            parameter("symbol", symbol)
            parameter("apikey", BuildConfig.TWELVE_DATA_API_KEY)
        }.body()
    }
}