package com.davidcrespo.onewallet.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.davidcrespo.onewallet.data.local.database.dao.PortfolioDao
import com.davidcrespo.onewallet.data.local.database.dao.PortfolioSnapshotDao
import com.davidcrespo.onewallet.data.local.database.entities.MonthlyPortfolioSnapshotEntity
import com.davidcrespo.onewallet.data.local.database.entities.PortfolioItemEntity

@Database(entities = [PortfolioItemEntity::class, MonthlyPortfolioSnapshotEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
    abstract fun portfolioSnapshotDao(): PortfolioSnapshotDao
}