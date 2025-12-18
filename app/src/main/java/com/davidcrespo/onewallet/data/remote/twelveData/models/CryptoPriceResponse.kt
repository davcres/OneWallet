package com.davidcrespo.onewallet.data.remote.twelveData.models

import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import kotlinx.serialization.Serializable

@Serializable
data class CryptoPriceResponse(val price: String)

fun CryptoPriceResponse.toInvestDto(symbol: String) = InvestmentDto(
    symbol = symbol,
    quantity = 0.0,
    price = price.toDouble(),
    previousPrice = 0.0,
    currency = if (symbol.endsWith("EUR")) Currency.EUR else Currency.USD,
    type = InvestmentType.CRYPTO,
    year = 0,
    month = 0
)
