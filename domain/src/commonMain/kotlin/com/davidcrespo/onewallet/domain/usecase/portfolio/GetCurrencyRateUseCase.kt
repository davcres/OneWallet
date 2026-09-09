package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.repository.FinancialRepository

class GetCurrencyRateUseCase(private val financialRepository: FinancialRepository) {
    suspend operator fun invoke(from: String, to: String): Result<Double> {
        return financialRepository.getRate(from, to).map { it.rate }
    }
}