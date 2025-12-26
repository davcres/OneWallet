package com.davidcrespo.onewallet.data.remote.stock.models

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import kotlinx.serialization.Serializable

@Serializable
data class MarketStockResponse(
    val symbol: String,
    val description: String,
    val currency: String,
    val figi: String,
    val type: String
)

fun MarketStockResponse.toDomain(): MarketAsset? {
    return if (currency.isNotEmpty()) {
        MarketAsset(
            symbol = symbol,
            price = 0.0,
            currency = Currency.USD,
            type = InvestmentType.STOCK,
            description = description,
            figi = figi,
            stockType = type
        )
    } else {
        null
    }
}
