package com.davidcrespo.onewallet.data.remote.twelveData

import com.davidcrespo.onewallet.data.remote.twelveData.models.RateResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class TwelveDataApiClient(
    private val client: HttpClient,
    private val apiKey: String
) {

    suspend fun getRate(from: String, to: String): RateResponse {
        return client.get(TwelveDataApiConfig.GetRate.PATH) {
            parameter(TwelveDataApiConfig.GetRate.FROM_TO, "$from/$to")
            parameter(TwelveDataApiConfig.GetRate.AMOUNT, 1)
            parameter(TwelveDataApiConfig.API_KEY, apiKey)
        }.body()
    }
}

