package com.davidcrespo.onewallet.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.davidcrespo.onewallet.data.local.database.entities.MonthlyPortfolioSnapshotEntity
import kotlinx.coroutines.flow.Flow

data class MonthlyBalance(
    val year: Int,
    val month: Int,
    val totalBalance: Double
)

@Dao
abstract class PortfolioSnapshotDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertSnapshots(snapshots: List<MonthlyPortfolioSnapshotEntity>)

    @Query("DELETE FROM monthly_portfolio_snapshots WHERE year = :year AND month = :month")
    abstract suspend fun deleteSnapshotsForMonth(year: Int, month: Int)

    @Transaction
    open suspend fun updateMonthlySnapshot(year: Int, month: Int, snapshots: List<MonthlyPortfolioSnapshotEntity>) {
        deleteSnapshotsForMonth(year, month)
        insertSnapshots(snapshots)
    }

    @Query("SELECT year, month, SUM(quantity * price) as totalBalance FROM monthly_portfolio_snapshots GROUP BY year, month ORDER BY year DESC, month DESC")
    abstract fun getMonthlyBalances(): Flow<List<MonthlyBalance>>

    @Query("SELECT * FROM monthly_portfolio_snapshots WHERE year = :year AND month = :month")
    abstract suspend fun getSnapshotDetails(year: Int, month: Int): List<MonthlyPortfolioSnapshotEntity>
}
