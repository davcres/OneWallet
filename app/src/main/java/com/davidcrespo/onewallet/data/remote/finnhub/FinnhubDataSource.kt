package com.davidcrespo.onewallet.data.remote.finnhub

import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.data.remote.finnhub.models.MarketCryptoResponse
import com.davidcrespo.onewallet.data.remote.finnhub.models.MarketStockResponse
import com.davidcrespo.onewallet.data.remote.finnhub.models.toInvestDto

class FinnhubDataSource(private val finnhubApiClient: FinnhubApiClient) {

    suspend fun getStocksSymbols(exchange: String): List<MarketStockResponse> {
        return finnhubApiClient.getStocksSymbols(exchange)
    }

    suspend fun getCryptoSymbols(exchange: String): List<MarketCryptoResponse> {
        return finnhubApiClient.getCryptoSymbols(exchange)
    }

    suspend fun getStockPrice(symbol: String): InvestmentDto {
        return finnhubApiClient.getStockPrice(symbol).toInvestDto(symbol)
    }
}