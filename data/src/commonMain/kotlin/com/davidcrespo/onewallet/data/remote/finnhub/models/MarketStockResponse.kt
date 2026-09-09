package com.davidcrespo.onewallet.data.remote.finnhub.models

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
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
    return if (currency.isNotEmpty() && currency == USD) {
        MarketAsset(
            symbol = symbol,
            price = 0.0,
            currency = Currency(currency),
            type = InvestmentType.STOCK,
            description = description,
            figi = figi,
            stockType = type
        )
    } else {
        null
    }
}
