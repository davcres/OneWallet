package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.repository.FinancialRepository

class GetUsdEurUseCase(private val financialRepository: FinancialRepository) {
    suspend operator fun invoke(): Result<Double> {
        return financialRepository.getUsdEur().map { it.rate }
    }
}