package com.davidcrespo.onewallet.data.remote.binance.models

import com.davidcrespo.onewallet.data.remote.dto.CurrencyDto
import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
import kotlinx.serialization.Serializable

@Serializable
data class CryptoPriceResponse(
    val symbol: String,
    val lastPrice: String, // Current price
    val prevClosePrice: String // Close price
)

fun CryptoPriceResponse.toInvestDto(name: String = "") = InvestmentDto(
    symbol = symbol,
    name = name.ifBlank { symbol },
    quantity = 0.0,
    price = lastPrice.toDoubleOrNull() ?: 0.0,
    previousPrice = prevClosePrice.toDoubleOrNull() ?: 0.0,
    currency = when {
        symbol.endsWith(EUR) -> CurrencyDto(EUR)
        symbol.endsWith(USD) -> CurrencyDto(USD)
        else -> CurrencyDto(USD)
    },
    type = InvestmentType.CRYPTO,
    year = 0,
    month = 0
)
