package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository
import java.time.LocalDate

class SaveMonthlyPortfolioUseCase(
    private val portfolioRepository: PortfolioRepository
) {
    suspend operator fun invoke(items: List<Investment>) {
        val now = LocalDate.now()
        val year = now.year
        val month = now.monthValue
        
        if (items.isEmpty()) {
            portfolioRepository.deleteMonthPortfolio(year, month)
            return
        }

        val investmentsEntities = items.map { it.setDate(month, year) }

        portfolioRepository.updateMonthPortfolio(year, month, investmentsEntities)
    }
}
