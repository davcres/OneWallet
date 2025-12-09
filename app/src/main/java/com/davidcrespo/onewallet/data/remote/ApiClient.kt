package com.davidcrespo.onewallet.data.remote

import com.davidcrespo.onewallet.BuildConfig
import com.davidcrespo.onewallet.data.models.PriceResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ApiClient(private val client: HttpClient) {

    suspend fun getPrice(symbol: String): PriceResponse {
        return client.get(ApiConfig.GetPrice.PATH) {
            parameter(ApiConfig.GetPrice.SYMBOL, symbol)
            parameter(ApiConfig.GetPrice.API_KEY, BuildConfig.TWELVE_DATA_API_KEY)
        }.body()
    }
}
