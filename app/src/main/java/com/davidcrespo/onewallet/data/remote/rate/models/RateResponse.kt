package com.davidcrespo.onewallet.data.remote.rate.models

import com.davidcrespo.onewallet.domain.model.rate.Rate
import kotlinx.serialization.Serializable

@Serializable
data class RateResponse(
    val symbol: String,
    val rate: Double
)

fun RateResponse.toDomain() = Rate(
    symbol = symbol,
    rate = rate
)