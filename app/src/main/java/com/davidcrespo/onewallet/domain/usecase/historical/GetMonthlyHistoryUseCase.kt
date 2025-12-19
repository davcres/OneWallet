package com.davidcrespo.onewallet.domain.usecase.historical

import com.davidcrespo.onewallet.data.local.database.dao.PortfolioDao
import com.davidcrespo.onewallet.data.local.database.entities.toDomain
import com.davidcrespo.onewallet.domain.model.investment.Investment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMonthlyHistoryUseCase(
    private val portfolioDao: PortfolioDao
) {
    operator fun invoke(): Flow<List<Investment>> {
        return portfolioDao.getMonthsPortfolio().map { it.map { it.toDomain() } }
    }
}