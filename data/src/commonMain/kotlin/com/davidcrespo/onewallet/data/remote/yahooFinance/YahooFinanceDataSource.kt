package com.davidcrespo.onewallet.data.remote.yahooFinance

import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.data.remote.yahooFinance.models.MarketStockResponse
import com.davidcrespo.onewallet.data.remote.yahooFinance.models.toInvestDto

class YahooFinanceDataSource(private val yahooFinanceApiClient: YahooFinanceApiClient) {

    suspend fun getStocksSymbolsByQuery(query: String): List<MarketStockResponse> {
        return yahooFinanceApiClient.getStocksSymbolsByQuery(query).quotes.orEmpty()
    }

    suspend fun getStockPrice(symbol: String, name: String): InvestmentDto? {
        return yahooFinanceApiClient.getStockPrice(symbol).chart?.result?.firstOrNull()?.meta?.toInvestDto(symbol, name)
    }
}