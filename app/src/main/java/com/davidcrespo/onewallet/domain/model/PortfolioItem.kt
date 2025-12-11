package com.davidcrespo.onewallet.domain.model

import com.davidcrespo.onewallet.domain.model.finnhub.StockInfo

data class PortfolioItem(
    val stockInfo: StockInfo,
    val quantity: Double,
    val sortOrder: Int,
    val currentPrice: Double? = null // Transient field, not persisted
)