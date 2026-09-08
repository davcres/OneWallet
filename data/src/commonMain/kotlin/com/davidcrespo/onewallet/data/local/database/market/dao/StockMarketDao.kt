package com.davidcrespo.onewallet.data.local.database.market.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.davidcrespo.onewallet.data.local.database.market.entities.StockMarketEntity

/**
 * Write/Update/Delete -> Suspend
 * Read -> Flow (always updated)
 * Read -> Suspend (One shot data)
 */
@Dao
abstract class StockMarketDao {

    @Query("SELECT * FROM stock_market_table")
    abstract suspend fun getAll(): List<StockMarketEntity>

    @Query("DELETE FROM stock_market_table")
    abstract suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(market: List<StockMarketEntity>)

    @Transaction
    open suspend fun replaceAll(market: List<StockMarketEntity>) {
        clear()
        insertAll(market)
    }
}
