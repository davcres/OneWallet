package com.davidcrespo.onewallet.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.davidcrespo.onewallet.data.local.database.converters.RoomConverters
import com.davidcrespo.onewallet.data.local.database.market.dao.CryptoMarketDao
import com.davidcrespo.onewallet.data.local.database.market.dao.StockMarketDao
import com.davidcrespo.onewallet.data.local.database.market.entities.CryptoMarketEntity
import com.davidcrespo.onewallet.data.local.database.market.entities.StockMarketEntity
import com.davidcrespo.onewallet.data.local.database.portfolio.dao.PortfolioDao
import com.davidcrespo.onewallet.data.local.database.portfolio.entities.InvestmentEntity

@Database(entities = [InvestmentEntity::class, StockMarketEntity::class, CryptoMarketEntity::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
    abstract fun stockMarketDao(): StockMarketDao
    abstract fun cryptoMarketDao(): CryptoMarketDao
}

@Suppress("KotlinNoActualForExpect", "NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
