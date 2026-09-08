package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SaveMonthlyPortfolioUseCase(
    private val portfolioRepository: PortfolioRepository,
    private val clock: Clock = Clock.System
) {
    suspend operator fun invoke(items: List<Investment>) {
        val now = clock.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val year = now.year
        val month = now.monthNumber
        
        if (items.isEmpty()) {
            portfolioRepository.deleteMonthPortfolio(year, month)
            return
        }

        val investmentsEntities = items.map { it.setDate(month, year) }

        portfolioRepository.updateMonthPortfolio(year, month, investmentsEntities)
    }
}
