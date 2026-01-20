package com.davidcrespo.onewallet.presentation.models

import androidx.compose.runtime.Immutable
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset

@Immutable
data class MarketAssetView(
    val symbol: String,
    val price: Double,
    val currency: Currency,
    val type: InvestmentType,
    val description: String?,
    val figi: String?,
    val stockType: String?
)

fun MarketAsset.toUI() = MarketAssetView(
    symbol = symbol,
    price = price,
    currency = currency,
    type = type,
    description = description,
    figi = figi,
    stockType = stockType
)

fun MarketAssetView.toDomain() = MarketAsset(
    symbol = symbol,
    price = price,
    currency = currency,
    type = type,
    description = description,
    figi = figi,
    stockType = stockType
)
