package com.davidcrespo.onewallet.data.remote.yahooFinance

import com.davidcrespo.onewallet.data.remote.yahooFinance.models.MarketStockResponseList
import com.davidcrespo.onewallet.data.remote.yahooFinance.models.StockPriceResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class YahooFinanceApiClient(private val client: HttpClient) {

    suspend fun getStocksSymbolsByQuery(query: String): MarketStockResponseList {
        return client.get(YahooFinanceApiConfig.GetSymbolsByQuery.PATH) {
            parameter(YahooFinanceApiConfig.GetSymbolsByQuery.QUERY, query)
        }.body()
    }

    suspend fun getStockPrice(symbol: String): StockPriceResponse {
        return client.get(YahooFinanceApiConfig.GetQuote.PATH+"/$symbol") {
            parameter(YahooFinanceApiConfig.GetQuote.INTERVAL, "1d")
            parameter(YahooFinanceApiConfig.GetQuote.RANGE, "1d")
        }.body()
    }
}
