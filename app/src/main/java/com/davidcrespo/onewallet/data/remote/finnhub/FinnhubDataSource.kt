package com.davidcrespo.onewallet.data.remote.finnhub

import com.davidcrespo.onewallet.data.remote.finnhub.models.QuoteResponse
import com.davidcrespo.onewallet.data.remote.finnhub.models.RateResponse
import com.davidcrespo.onewallet.data.remote.finnhub.models.StockInfoResponse

class FinnhubDataSource(private val finnhubApiClient: FinnhubApiClient) {

    suspend fun getSymbols(exchange: String): List<StockInfoResponse> {
        return finnhubApiClient.getSymbols(exchange)
    }

    suspend fun getPrice(symbol: String): QuoteResponse {
        return finnhubApiClient.getQuote(symbol)
    }

    suspend fun getUsdEur(): RateResponse {
        return finnhubApiClient.getUsdEur()
    }
}