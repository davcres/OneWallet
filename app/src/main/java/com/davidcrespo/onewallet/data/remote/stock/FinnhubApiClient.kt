package com.davidcrespo.onewallet.data.remote.stock

import com.davidcrespo.onewallet.BuildConfig
import com.davidcrespo.onewallet.data.remote.stock.models.MarketStockResponse
import com.davidcrespo.onewallet.data.remote.stock.models.StockPriceResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class FinnhubApiClient(private val client: HttpClient) {

    suspend fun getStocksSymbols(exchange: String): List<MarketStockResponse> {
        return client.get("${FinnhubApiConfig.BASE_URL}/${FinnhubApiConfig.GetSymbols.PATH}") {
            parameter(FinnhubApiConfig.GetSymbols.EXCHANGE, exchange)
            parameter(FinnhubApiConfig.GetSymbols.TOKEN, BuildConfig.FINNHUB_API_KEY)
        }.body()
    }

    suspend fun getStockPrice(symbol: String): StockPriceResponse {
        return client.get("${FinnhubApiConfig.BASE_URL}/${FinnhubApiConfig.GetQuote.PATH}") {
            parameter(FinnhubApiConfig.GetQuote.SYMBOL, symbol)
            parameter(FinnhubApiConfig.GetQuote.TOKEN, BuildConfig.FINNHUB_API_KEY)
        }.body()
    }
}
