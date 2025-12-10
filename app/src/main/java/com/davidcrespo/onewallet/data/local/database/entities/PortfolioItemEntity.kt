package com.davidcrespo.onewallet.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.davidcrespo.onewallet.domain.model.finnhub.StockInfo

@Entity(tableName = "portfolio_items")
data class PortfolioItemEntity(
    @PrimaryKey
    val displaySymbol: String,
    val description: String,
    val currency: String,
    val figi: String,
    val isin: String,
    val type: String,
    val quantity: Double,
    val sortOrder: Int
)

fun PortfolioItemEntity.toDomain() = StockInfo(
    currency = currency,
    description = description,
    displaySymbol = displaySymbol,
    figi = figi,
    isin = isin,
    type = type
)

fun StockInfo.toEntity(quantity: Double, sortOrder: Int) = PortfolioItemEntity(
    displaySymbol = displaySymbol,
    description = description,
    currency = currency,
    figi = figi,
    isin = isin,
    type = type,
    quantity = quantity,
    sortOrder = sortOrder
)
