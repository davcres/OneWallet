package com.davidcrespo.onewallet.data.remote.twelveData

import com.davidcrespo.onewallet.BuildConfig
import com.davidcrespo.onewallet.data.remote.twelveData.models.RateResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class TwelveDataApiClient(private val client: HttpClient) {

    suspend fun getUsdEur(): RateResponse {
        return client.get(TwelveDataApiConfig.GetRate.PATH) {
            parameter(TwelveDataApiConfig.GetRate.FROM_TO, TwelveDataApiConfig.GetRate.USD_EUR)
            parameter(TwelveDataApiConfig.GetRate.AMOUNT, 1)
            parameter(TwelveDataApiConfig.GetRate.API_KEY, BuildConfig.TWELVE_DATA_API_KEY)
        }.body()
    }
}
