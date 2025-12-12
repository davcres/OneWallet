package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.data.local.database.dao.PortfolioSnapshotDao
import com.davidcrespo.onewallet.data.local.database.entities.MonthlyPortfolioSnapshotEntity
import com.davidcrespo.onewallet.domain.model.PortfolioItem
import java.time.LocalDate

class SaveMonthlySnapshotUseCase(
    private val portfolioSnapshotDao: PortfolioSnapshotDao
) {
    suspend operator fun invoke(items: List<PortfolioItem>) {
        val now = LocalDate.now()
        val year = now.year
        val month = now.monthValue
        
        if (items.isEmpty()) {
            portfolioSnapshotDao.deleteSnapshotsForMonth(year, month)
            return
        }

        val timestamp = System.currentTimeMillis()

        val snapshots = items.mapNotNull { item ->
            val price = item.currentPrice ?: 0.0
            if (price > 0.0) {
                MonthlyPortfolioSnapshotEntity(
                    year = year,
                    month = month,
                    symbol = item.stockInfo.displaySymbol,
                    quantity = item.quantity,
                    price = price,
                    currency = "EUR", // ViewModel normalizes to EUR
                    timestamp = timestamp
                )
            } else {
                null
            }
        }

        if (snapshots.isNotEmpty()) {
            portfolioSnapshotDao.updateMonthlySnapshot(year, month, snapshots)
        }
    }
}
