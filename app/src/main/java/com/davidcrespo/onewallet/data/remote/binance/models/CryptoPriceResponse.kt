package com.davidcrespo.onewallet.data.remote.binance.models

import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import kotlinx.serialization.Serializable

@Serializable
data class CryptoPriceResponse(
    val symbol: String,
    val lastPrice: String, // Current price
    val prevClosePrice: String // Close price
)

fun CryptoPriceResponse.toInvestDto() = InvestmentDto(
    symbol = symbol,
    name = "",
    quantity = 0.0,
    price = lastPrice.toDoubleOrNull() ?: 0.0,
    previousPrice = prevClosePrice.toDoubleOrNull() ?: 0.0,
    currency = if (symbol.endsWith("EUR")) Currency.EUR else Currency.USD,
    type = InvestmentType.CRYPTO,
    year = 0,
    month = 0
)
