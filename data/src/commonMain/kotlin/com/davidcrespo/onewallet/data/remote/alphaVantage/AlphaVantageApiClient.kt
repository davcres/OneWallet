package com.davidcrespo.onewallet.data.remote.alphaVantage

import com.davidcrespo.onewallet.data.remote.alphaVantage.models.MarketStockResponseList
import com.davidcrespo.onewallet.data.remote.alphaVantage.models.StockPriceResponseObject
import io.ktor.client.request.parameter

class AlphaVantageApiClient(private val client: AlphaVantageHttpClient) {

    suspend fun getStocksSymbolsByQuery(query: String): MarketStockResponseList {
        return client.get(AlphaVantageApiConfig.GetSymbolsByQuery.PATH) {
            parameter(
                AlphaVantageApiConfig.GetSymbolsByQuery.FUNCTION,
                AlphaVantageApiConfig.GetSymbolsByQuery.SYMBOL_SEARCH
            )
            parameter(AlphaVantageApiConfig.GetSymbolsByQuery.KEYWORDS, query)
        }
    }

    suspend fun getStockPrice(symbol: String): StockPriceResponseObject {
        return client.get(AlphaVantageApiConfig.GetQuote.PATH) {
            parameter(
                AlphaVantageApiConfig.GetQuote.FUNCTION,
                AlphaVantageApiConfig.GetQuote.GLOBAL_QUOTE
            )
            parameter(AlphaVantageApiConfig.GetQuote.SYMBOL, symbol)
        }
    }
}
