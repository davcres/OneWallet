package com.davidcrespo.onewallet.data.remote.finnhub.models

import com.davidcrespo.onewallet.domain.model.finnhub.StockInfo
import kotlinx.serialization.Serializable

@Serializable
data class CryptoSymbolResponse(
    val description: String,
    val displaySymbol: String,
    val symbol: String
)

fun CryptoSymbolResponse.toDomain() = StockInfo(
    currency = if (displaySymbol.endsWith("EUR")) "EUR" else "USD",
    description = description,
    displaySymbol = displaySymbol.replace("USDC", "USD"),
    figi = "",
    isin = "",
    type = "CRYPTO"
)
