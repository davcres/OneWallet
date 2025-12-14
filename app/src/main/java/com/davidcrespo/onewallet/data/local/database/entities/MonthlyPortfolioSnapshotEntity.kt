package com.davidcrespo.onewallet.data.local.database.entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "monthly_portfolio_snapshots", primaryKeys = ["year", "month", "symbol"])
data class MonthlyPortfolioSnapshotEntity(
    val year: Int,
    val month: Int,
    val symbol: String,
    val quantity: Double,
    val price: Double,
    val currency: String,
    val timestamp: Long
)
