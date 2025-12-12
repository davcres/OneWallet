package com.davidcrespo.onewallet.data.remote.finnhub.models

import com.davidcrespo.onewallet.domain.model.finnhub.Rate
import kotlinx.serialization.Serializable

@Serializable
data class RateResponse(
    val base: String,
    val symbol: String,
    val price: Double
)

fun RateResponse.toDomain() = Rate(
    base = base,
    symbol = symbol,
    price = price
)