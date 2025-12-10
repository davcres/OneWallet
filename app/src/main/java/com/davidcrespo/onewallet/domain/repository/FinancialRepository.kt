package com.davidcrespo.onewallet.domain.repository

import com.davidcrespo.onewallet.domain.model.twelveData.Price
import com.davidcrespo.onewallet.domain.model.finnhub.Quote
import com.davidcrespo.onewallet.domain.model.finnhub.StockInfo

interface FinancialRepository {
    suspend fun getPrice(symbol: String): Result<Price>
    suspend fun getSymbols(exchange: String): Result<List<StockInfo>>
    suspend fun getQuote(symbol: String): Result<Quote>
}
