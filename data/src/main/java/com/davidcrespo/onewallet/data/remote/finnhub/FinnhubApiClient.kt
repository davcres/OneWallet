package com.davidcrespo.onewallet.data.remote.finnhub

import com.davidcrespo.onewallet.data.remote.finnhub.models.MarketStockResponse
import com.davidcrespo.onewallet.data.remote.finnhub.models.StockPriceResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class FinnhubApiClient(private val client: HttpClient) {

    suspend fun getStocksSymbols(exchange: String): List<MarketStockResponse> {
        return client.get(FinnhubApiConfig.GetSymbols.PATH) {
            parameter(FinnhubApiConfig.GetSymbols.EXCHANGE, exchange)
        }.body()
    }

    suspend fun getStockPrice(symbol: String): StockPriceResponse {
        return client.get(FinnhubApiConfig.GetQuote.PATH) {
            parameter(FinnhubApiConfig.GetQuote.SYMBOL, symbol)
        }.body()
    }
}
