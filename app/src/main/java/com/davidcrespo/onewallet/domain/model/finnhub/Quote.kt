package com.davidcrespo.onewallet.domain.model.finnhub

data class Quote(
    val currentPrice: Double,
    val changePercent: Double? = null,
)