package com.davidcrespo.onewallet.data.remote.finnhub

import com.davidcrespo.onewallet.data.remote.finnhub.models.QuoteResponse

class FinnhubDataSource(private val finnhubApiClient: FinnhubApiClient) {

    suspend fun getPrice(symbol: String): QuoteResponse {
        return finnhubApiClient.getQuote(symbol)
    }
}
