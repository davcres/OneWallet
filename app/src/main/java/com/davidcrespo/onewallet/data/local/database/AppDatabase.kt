package com.davidcrespo.onewallet.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.davidcrespo.onewallet.data.local.database.converters.RoomConverters
import com.davidcrespo.onewallet.data.local.database.dao.PortfolioDao
import com.davidcrespo.onewallet.data.local.database.entities.InvestmentEntity

@Database(entities = [InvestmentEntity::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
}
