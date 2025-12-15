package com.davidcrespo.onewallet.domain.usecase

import com.davidcrespo.onewallet.domain.model.finnhub.StockInfo
import com.davidcrespo.onewallet.domain.repository.FinancialRepository

class GetSymbolsUseCase(private val repository: FinancialRepository) {
    suspend operator fun invoke(exchange: String, isCrypto: Boolean = false): Result<List<StockInfo>> {
        return if (isCrypto) {
            repository.getCryptoSymbols(exchange)
        } else {
            repository.getSymbols(exchange)
        }
    }
}
