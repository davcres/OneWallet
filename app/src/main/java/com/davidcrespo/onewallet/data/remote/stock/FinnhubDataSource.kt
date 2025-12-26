package com.davidcrespo.onewallet.data.remote.stock

import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.data.remote.stock.models.MarketStockResponse
import com.davidcrespo.onewallet.data.remote.stock.models.toInvestDto

class FinnhubDataSource(private val finnhubApiClient: FinnhubApiClient) {

    suspend fun getStocksSymbols(exchange: String): List<MarketStockResponse> {
        return finnhubApiClient.getStocksSymbols(exchange)
    }

    suspend fun getStockPrice(symbol: String): InvestmentDto {
        return finnhubApiClient.getStockPrice(symbol).toInvestDto(symbol)
    }
}