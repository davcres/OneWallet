package com.davidcrespo.onewallet.data.remote.finnhub.models

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import kotlinx.serialization.Serializable

@Serializable
data class MarketCryptoResponse(
    val displaySymbol: String,
)

fun MarketCryptoResponse.toDomain() = MarketAsset(
    symbol = displaySymbol.replace("USDC", "USD"),
    currency = if (displaySymbol.endsWith("EUR")) Currency.EUR else Currency.USD,
    type = InvestmentType.CRYPTO,
    description = null,
    figi = null,
    stockType = null
)
