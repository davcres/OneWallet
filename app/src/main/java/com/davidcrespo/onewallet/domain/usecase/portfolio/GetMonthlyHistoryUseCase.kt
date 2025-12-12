package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.data.local.database.dao.MonthlyBalance
import com.davidcrespo.onewallet.data.local.database.dao.PortfolioSnapshotDao
import kotlinx.coroutines.flow.Flow

class GetMonthlyHistoryUseCase(
    private val portfolioSnapshotDao: PortfolioSnapshotDao
) {
    operator fun invoke(): Flow<List<MonthlyBalance>> {
        return portfolioSnapshotDao.getMonthlyBalances()
    }
}
