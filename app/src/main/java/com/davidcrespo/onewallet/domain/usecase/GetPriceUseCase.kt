package com.davidcrespo.onewallet.domain.usecase

import com.davidcrespo.onewallet.domain.model.Price
import com.davidcrespo.onewallet.domain.repository.FinancialRepository

class GetPriceUseCase(private val repository: FinancialRepository) {
    suspend operator fun invoke(): Result<Price> {
        return repository.getPrice()
    }
}
