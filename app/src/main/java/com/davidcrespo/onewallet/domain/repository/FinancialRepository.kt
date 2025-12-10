package com.davidcrespo.onewallet.domain.repository

import com.davidcrespo.onewallet.domain.model.Price
import com.davidcrespo.onewallet.domain.model.Quote

interface FinancialRepository {
    suspend fun getPrice(symbol: String): Result<Price>
    suspend fun getQuote(symbol: String): Result<Quote>
}
