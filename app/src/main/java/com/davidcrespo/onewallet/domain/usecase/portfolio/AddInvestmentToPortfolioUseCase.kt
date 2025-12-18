package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository

class AddInvestmentToPortfolioUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(investment: Investment) {
        repository.addOrUpdateItem(investment)
    }
}
