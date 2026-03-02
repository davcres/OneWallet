package com.davidcrespo.onewallet.data.remote.alphaVantage

import com.davidcrespo.onewallet.data.remote.alphaVantage.models.MarketStockResponse
import com.davidcrespo.onewallet.data.remote.alphaVantage.models.toInvestDto
import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.domain.model.investment.Currency

class AlphaVantageDataSource(private val alphaVantageApiClient: AlphaVantageApiClient) {

    suspend fun getStocksSymbolsByQuery(query: String): List<MarketStockResponse> {
        return alphaVantageApiClient.getStocksSymbolsByQuery(query).bestMatches
    }

    suspend fun getStockPrice(symbol: String, name: String, currency: Currency): InvestmentDto {
        return alphaVantageApiClient.getStockPrice(symbol).globalQuote.toInvestDto(symbol, name, currency)
    }
}