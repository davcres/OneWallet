package com.davidcrespo.onewallet.data.remote.finnhub.models

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import kotlinx.serialization.Serializable

@Serializable
data class MarketStockResponse(
    val symbol: String,
    val description: String,
    val currency: Currency,
    val figi: String,
    val type: String
)

fun MarketStockResponse.toDomain() = MarketAsset(
    symbol = symbol,
    currency = currency,
    type = InvestmentType.STOCK,
    description = description,
    figi = figi,
    stockType = type
)
