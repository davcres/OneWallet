package com.davidcrespo.onewallet.data.remote.finnhub.models

import com.davidcrespo.onewallet.domain.model.finnhub.StockInfo
import kotlinx.serialization.Serializable

@Serializable
data class StockInfoResponse(
    val currency: String,
    val description: String,
    val displaySymbol: String,
    val figi: String,
    val isin: String,
    val mic: String,
    val shareClassFIGI: String,
    val symbol: String,
    val symbol2: String,
    val type: String
)

fun StockInfoResponse.toDomain() = StockInfo(
    currency = currency,
    description = description,
    displaySymbol = displaySymbol,
    figi = figi,
    type = type
)
