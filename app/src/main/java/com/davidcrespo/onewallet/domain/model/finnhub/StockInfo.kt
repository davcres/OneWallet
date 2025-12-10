package com.davidcrespo.onewallet.domain.model.finnhub

data class StockInfo(
    val currency: String,
    val description: String,
    val displaySymbol: String,
    val figi: String,
    val type: String
)
