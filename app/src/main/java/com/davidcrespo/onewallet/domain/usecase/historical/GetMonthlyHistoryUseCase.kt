package com.davidcrespo.onewallet.domain.usecase.historical

import com.davidcrespo.onewallet.data.local.database.portfolio.dao.PortfolioDao
import com.davidcrespo.onewallet.data.local.database.portfolio.entities.toDomain
import com.davidcrespo.onewallet.domain.model.investment.Investment

class GetMonthlyHistoryUseCase(
    private val portfolioDao: PortfolioDao
) {
    suspend operator fun invoke(): Result<List<Investment>> {
        return runCatching {
            Result.success(
            portfolioDao.getMonthsPortfolio().map { it.toDomain() }
            )
        }.getOrElse {
            Result.failure(it)
        }
    }
}