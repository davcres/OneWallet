package com.davidcrespo.onewallet.data.remote.twelveData.models

import com.davidcrespo.onewallet.domain.model.twelveData.Price
import kotlinx.serialization.Serializable

@Serializable
data class PriceResponse(val price: String)

fun PriceResponse.toDomain() = Price(price.toBigDecimal())