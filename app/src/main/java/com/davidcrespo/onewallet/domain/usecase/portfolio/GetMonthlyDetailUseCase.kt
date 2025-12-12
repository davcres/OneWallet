package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.data.local.database.dao.PortfolioSnapshotDao
import com.davidcrespo.onewallet.data.local.database.entities.MonthlyPortfolioSnapshotEntity

class GetMonthlyDetailUseCase(
    private val portfolioSnapshotDao: PortfolioSnapshotDao
) {
    suspend operator fun invoke(year: Int, month: Int): List<MonthlyPortfolioSnapshotEntity> {
        return portfolioSnapshotDao.getSnapshotDetails(year, month)
    }
}
