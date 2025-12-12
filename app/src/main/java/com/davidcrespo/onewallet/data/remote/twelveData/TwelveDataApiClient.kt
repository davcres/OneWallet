package com.davidcrespo.onewallet.data.remote.twelveData

import com.davidcrespo.onewallet.BuildConfig
import com.davidcrespo.onewallet.data.remote.twelveData.models.RateResponse
import com.davidcrespo.onewallet.data.remote.twelveData.models.PriceResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class TwelveDataApiClient(private val client: HttpClient) {

    suspend fun getPrice(symbol: String): PriceResponse {
        return client.get("${TwelveDataApiConfig.BASE_URL}/${TwelveDataApiConfig.GetPrice.PATH}") {
            parameter(TwelveDataApiConfig.GetPrice.SYMBOL, symbol)
            parameter(TwelveDataApiConfig.GetPrice.API_KEY, BuildConfig.TWELVE_DATA_API_KEY)
        }.body()
    }

    suspend fun getUsdEur(): RateResponse {
        return client.get("${TwelveDataApiConfig.BASE_URL}/${TwelveDataApiConfig.GetRate.PATH}") {
            parameter(TwelveDataApiConfig.GetRate.FROM_TO, "USD/EUR")
            parameter(TwelveDataApiConfig.GetRate.AMOUNT, 1)
            parameter(TwelveDataApiConfig.GetRate.API_KEY, BuildConfig.TWELVE_DATA_API_KEY)
        }.body()
    }
}
