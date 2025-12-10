package com.davidcrespo.onewallet.domain.usecase

import com.davidcrespo.onewallet.domain.model.twelveData.Price
import com.davidcrespo.onewallet.domain.repository.FinancialRepository

class GetPriceUseCase(private val repository: FinancialRepository) {
    suspend operator fun invoke(symbol: String): Result<Price> {
        return repository.getPrice(symbol)
    }
}
