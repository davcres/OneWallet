package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.repository.FinancialRepository

class GetInvestmentPriceUseCase(private val repository: FinancialRepository) {
    suspend operator fun invoke(symbol: String, type: InvestmentType, name: String = ""): Result<Investment> {
        return repository.getInvestmentPrice(symbol, type, name)
    }
}