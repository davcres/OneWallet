package com.davidcrespo.onewallet.data.remote.binance

import com.davidcrespo.onewallet.data.remote.binance.models.CryptoPriceResponse
import com.davidcrespo.onewallet.data.remote.binance.models.MarketCryptoResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class BinanceApiClient(private val client: HttpClient) {

    suspend fun getCryptoSymbols(): List<MarketCryptoResponse> {
        return client
            .get(BinanceApiConfig.GetCryptoSymbols.PATH)
            .body()
    }

    suspend fun getCryptoPrice(symbol: String): CryptoPriceResponse {
        return client.get(BinanceApiConfig.GetCryptoPrice.PATH) {
            parameter(BinanceApiConfig.GetCryptoPrice.SYMBOL, symbol)
        }.body()
    }
}
