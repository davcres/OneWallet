package com.davidcrespo.onewallet.domain.usecase

import com.davidcrespo.onewallet.domain.model.finnhub.Quote
import com.davidcrespo.onewallet.domain.repository.FinancialRepository

class GetQuoteUseCase(private val repository: FinancialRepository) {
    suspend operator fun invoke(symbol: String): Result<Quote> {
        return repository.getQuote(symbol)
    }
}
