package com.davidcrespo.onewallet.domain.usecase

import com.davidcrespo.onewallet.domain.model.finnhub.StockInfo
import com.davidcrespo.onewallet.domain.repository.FinancialRepository

class GetSymbolsUseCase(private val repository: FinancialRepository) {
    suspend operator fun invoke(exchange: String): Result<List<StockInfo>> {
        return repository.getSymbols(exchange)
    }
}
