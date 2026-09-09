package com.davidcrespo.onewallet.data.remote.binance.models

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import kotlinx.serialization.Serializable

@Serializable
data class MarketCryptoResponse(
    val symbol: String,
    val price: String
)

fun MarketCryptoResponse.toDomain() = MarketAsset(
    symbol = symbol,
    price = price.toDouble(),
    currency = when {
        symbol.endsWith(EUR) -> Currency(EUR)
        symbol.endsWith(USD) -> Currency(USD)
        else -> Currency(USD)
    },
    type = InvestmentType.CRYPTO,
    description = null,
    figi = null,
    stockType = null
)
