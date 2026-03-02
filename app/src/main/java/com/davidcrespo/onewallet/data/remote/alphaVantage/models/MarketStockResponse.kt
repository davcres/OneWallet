package com.davidcrespo.onewallet.data.remote.alphaVantage.models

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.GlobalMarketRegion
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MarketStockResponseList(
    @SerialName("bestMatches")
    val bestMatches: List<MarketStockResponse>
)

@Serializable
data class MarketStockResponse(
    @SerialName("1. symbol")
    val symbol: String,

    @SerialName("2. name")
    val name: String,

    @SerialName("3. type")
    val type: String,

    @SerialName("4. region")
    val region: String,

    @SerialName("5. marketOpen")
    val marketOpen: String,

    @SerialName("6. marketClose")
    val marketClose: String,

    @SerialName("7. timezone")
    val timezone: String,

    @SerialName("8. currency")
    val currency: String,

    @SerialName("9. matchScore")
    val matchScore: String
)

fun MarketStockResponse.toDomain(): MarketAsset {
    return MarketAsset(
        symbol = symbol,
        price = 0.0,
        currency = Currency.from(currency),
        type = InvestmentType.STOCK,
        description = name,
        region = GlobalMarketRegion.from(region),
        stockType = type
    )
}
