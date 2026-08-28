package com.davidcrespo.onewallet.data.remote.finnhub

import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.data.remote.finnhub.models.MarketStockResponse
import com.davidcrespo.onewallet.data.remote.finnhub.models.toInvestDto

class FinnhubDataSource(private val finnhubApiClient: FinnhubApiClient) {

    suspend fun getStocksSymbols(exchange: String): List<MarketStockResponse> {
        return finnhubApiClient.getStocksSymbols(exchange)
    }

    suspend fun getStockPrice(symbol: String, name: String): InvestmentDto {
        return finnhubApiClient.getStockPrice(symbol).toInvestDto(symbol, name)
    }
}