package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.data.local.database.dao.PortfolioDao
import com.davidcrespo.onewallet.data.local.database.entities.toEntity
import com.davidcrespo.onewallet.domain.model.investment.Investment
import java.time.LocalDate

class SaveMonthlyPortfolioUseCase(
    private val portfolioDao: PortfolioDao
) {
    suspend operator fun invoke(items: List<Investment>) {
        val now = LocalDate.now()
        val year = now.year
        val month = now.monthValue
        
        if (items.isEmpty()) {
            portfolioDao.deleteMonthPortfolio(year, month)
            return
        }

        val investmentsEntities = items.map { it.setDate(month, year).toEntity() }

        portfolioDao.updateMonthPortfolio(year, month, investmentsEntities)
    }
}
