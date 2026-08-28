package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.cache.SymbolCache
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository
import java.time.LocalDate

class RemovePortfolioItemUseCase(
    private val repository: PortfolioRepository,
    private val symbolCache: SymbolCache
) {
    suspend operator fun invoke(investment: Investment) {
        val now = LocalDate.now()
        val year = now.year
        val month = now.monthValue

        repository.removeItem(investment, year, month)
        symbolCache.removeCachedInvestment(investment.symbol)
    }
}
