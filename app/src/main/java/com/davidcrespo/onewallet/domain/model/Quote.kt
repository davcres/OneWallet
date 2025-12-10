package com.davidcrespo.onewallet.domain.model

data class Quote(
    val currentPrice: Double,
    val changePercent: Double? = null,
)
