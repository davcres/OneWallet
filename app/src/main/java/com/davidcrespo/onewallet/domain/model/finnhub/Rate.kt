package com.davidcrespo.onewallet.domain.model.finnhub

data class Rate(
    val base: String,
    val symbol: String,
    val price: Double
)
