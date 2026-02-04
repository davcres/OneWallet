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
) {

    override fun toString(): String {
        return "$symbol|$price|$currency|$type|${description.orEmpty()}|${figi.orEmpty()}|${stockType.orEmpty()}"
    }
}

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

fun String.toMarketAssetView(): MarketAssetView {
    val parts = this.split("|")
    fun emptyToNull(s: String) = s.takeIf { it.isNotEmpty() }

    return MarketAssetView(
        symbol = parts[0],
        price = parts[1].toDoubleOrNull() ?: 0.0,
        currency = Currency.valueOf(parts[2]),
        type = InvestmentType.valueOf(parts[3]),
        description = emptyToNull(parts[4]),
        figi = emptyToNull(parts[5]),
        stockType = emptyToNull(parts[6]),
    )
}
