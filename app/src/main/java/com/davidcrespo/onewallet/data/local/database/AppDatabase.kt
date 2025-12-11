package com.davidcrespo.onewallet.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.davidcrespo.onewallet.data.local.database.dao.PortfolioDao
import com.davidcrespo.onewallet.data.local.database.entities.PortfolioItemEntity

@Database(entities = [PortfolioItemEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
}