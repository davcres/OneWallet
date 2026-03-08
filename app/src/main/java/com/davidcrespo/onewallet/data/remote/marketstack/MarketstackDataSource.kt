package com.davidcrespo.onewallet.data.remote.marketstack

import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.data.remote.marketstack.models.MarketStockResponse
import com.davidcrespo.onewallet.data.remote.marketstack.models.toInvestDto

class MarketstackDataSource(private val marketstackApiClient: MarketstackApiClient) {

    suspend fun getStocksSymbolsByQuery(query: String): List<MarketStockResponse> {
        return marketstackApiClient.getStocksSymbolsByQuery(query).data.orEmpty()
    }

    suspend fun getStockPrice(symbol: String, name: String): InvestmentDto {
        return marketstackApiClient.getStockPrice(symbol).toInvestDto(symbol, name)
    }
}