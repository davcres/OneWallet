package com.davidcrespo.onewallet.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.davidcrespo.onewallet.data.local.database.entities.PortfolioItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PortfolioDao {
    @Query("SELECT * FROM portfolio_items ORDER BY sortOrder ASC")
    fun getAllPortfolioItems(): Flow<List<PortfolioItemEntity>>

    @Query("SELECT * FROM portfolio_items WHERE displaySymbol = :symbol LIMIT 1")
    suspend fun getItem(symbol: String): PortfolioItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: PortfolioItemEntity)

    @Delete
    suspend fun delete(item: PortfolioItemEntity)

    @Query("SELECT MAX(sortOrder) FROM portfolio_items")
    suspend fun getMaxSortOrder(): Int?

    @Update
    suspend fun updateItems(items: List<PortfolioItemEntity>)

    @Transaction
    suspend fun updatePortfolioOrder(items: List<PortfolioItemEntity>) {
        updateItems(items)
    }
}
