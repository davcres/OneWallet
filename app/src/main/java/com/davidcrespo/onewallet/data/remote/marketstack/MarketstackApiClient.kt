package com.davidcrespo.onewallet.data.remote.marketstack

import com.davidcrespo.onewallet.data.remote.marketstack.models.MarketStockResponseList
import com.davidcrespo.onewallet.data.remote.marketstack.models.StockPriceResponseList
import com.davidcrespo.onewallet.di.MarketstackHttpClient
import io.ktor.client.request.parameter

class MarketstackApiClient(private val client: MarketstackHttpClient) {

    suspend fun getStocksSymbolsByQuery(query: String): MarketStockResponseList {
        return client.get(MarketstackApiConfig.GetSymbolsByQuery.PATH) {
            parameter(MarketstackApiConfig.GetSymbolsByQuery.QUERY, query)
        }
    }

    suspend fun getStockPrice(symbol: String): StockPriceResponseList {
        return client.get(MarketstackApiConfig.GetQuote.PATH) {
            parameter(MarketstackApiConfig.GetQuote.SYMBOL, symbol)
            parameter(MarketstackApiConfig.GetQuote.LIMIT, 2)
            parameter(MarketstackApiConfig.GetQuote.SORT, "DESC")
        }
    }
}
