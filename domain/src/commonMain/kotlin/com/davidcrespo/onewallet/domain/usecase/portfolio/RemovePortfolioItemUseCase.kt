package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.cache.SymbolCache
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class RemovePortfolioItemUseCase(
    private val repository: PortfolioRepository,
    private val symbolCache: SymbolCache,
    private val clock: Clock = Clock.System
) {
    suspend operator fun invoke(investment: Investment) {
        val now = clock.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val year = now.year
        val month = now.monthNumber

        repository.removeItem(investment, year, month)
        symbolCache.removeCachedInvestment(investment.symbol)
    }
}
