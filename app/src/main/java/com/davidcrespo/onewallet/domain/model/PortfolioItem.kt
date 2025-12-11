package com.davidcrespo.onewallet.domain.model

import com.davidcrespo.onewallet.domain.model.finnhub.StockInfo

data class PortfolioItem(
    val stockInfo: StockInfo,
    val quantity: Double,
    val sortOrder: Int,
    val dcaAmount: Double = 0.0,
    val dcaFrequency: String = "Mensual", // Diario, Semanal, Mensual
    val dcaStartDate: Long? = null,
    val dcaInitialInvestment: Double = 0.0
)
