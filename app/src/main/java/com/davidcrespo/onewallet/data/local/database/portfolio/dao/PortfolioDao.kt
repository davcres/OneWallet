package com.davidcrespo.onewallet.data.local.database.portfolio.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.davidcrespo.onewallet.data.local.database.portfolio.entities.InvestmentEntity
import kotlinx.coroutines.flow.Flow

/**
 * Write/Update/Delete -> Suspend
 * Read -> Flow (always updated)
 * Read -> Suspend (One shot data)
 */
@Dao
abstract class PortfolioDao {

    @Query("SELECT * FROM monthly_portfolio_table WHERE symbol = :symbol AND year = :year AND month = :month LIMIT 1")
    abstract fun getItem(symbol: String, year: Int, month: Int): Flow<InvestmentEntity?>

    @Query("SELECT * FROM monthly_portfolio_table ORDER BY year DESC, month DESC")
    abstract fun getMonthsPortfolio(): Flow<List<InvestmentEntity>>

    @Query("SELECT * FROM monthly_portfolio_table WHERE year = :year AND month = :month")
    abstract fun getPortfolio(year: Int, month: Int): Flow<List<InvestmentEntity>>

    @Query("SELECT * FROM monthly_portfolio_table WHERE (year, month) = (SELECT year, month FROM monthly_portfolio_table ORDER BY year DESC, month DESC LIMIT 1)")
    abstract fun getLatestPortfolio(): Flow<List<InvestmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertPortfolio(investments: List<InvestmentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertOrUpdate(item: InvestmentEntity)

    @Transaction
    open suspend fun updateMonthPortfolio(year: Int, month: Int, investments: List<InvestmentEntity>) {
        deleteMonthPortfolio(year, month)
        insertPortfolio(investments)
    }

    @Query("DELETE FROM monthly_portfolio_table WHERE symbol = :symbol AND year = :year AND month = :month")
    abstract suspend fun deleteInvestment(symbol: String, year: Int, month: Int)

    @Query("DELETE FROM monthly_portfolio_table WHERE year = :year AND month = :month")
    abstract suspend fun deleteMonthPortfolio(year: Int, month: Int)
}
