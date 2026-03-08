package com.davidcrespo.onewallet.data.remote.marketstack.models

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.GlobalMarketRegion
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.UNKNOWN
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MarketStockResponseList(
    @SerialName("pagination")
    val pagination: Pagination? = null,

    @SerialName("data")
    val data: List<MarketStockResponse>? = null
)

@Serializable
data class Pagination(
    @SerialName("limit")
    val limit: Int? = null,

    @SerialName("offset")
    val offset: Int? = null,

    @SerialName("count")
    val count: Int? = null,

    @SerialName("total")
    val total: Int? = null
)

@Serializable
data class MarketStockResponse(
    @SerialName("name")
    val name: String? = null,

    @SerialName("ticker")
    val ticker: String? = null,

    @SerialName("has_intraday")
    val hasIntraday: Boolean? = null,

    @SerialName("has_eod")
    val hasEod: Boolean? = null,

    @SerialName("stock_exchange")
    val stockExchange: StockExchange? = null
)

@Serializable
data class StockExchange(
    @SerialName("name")
    val name: String? = null,

    @SerialName("acronym")
    val acronym: String? = null,

    @SerialName("mic")
    val mic: String? = null
)

fun MarketStockResponse.toDomain(): MarketAsset {
    return MarketAsset(
        symbol = ticker.orEmpty(),
        price = 0.0,
        currency = Currency(UNKNOWN),
        type = InvestmentType.STOCK,
        description = name,
        region = GlobalMarketRegion.from(stockExchange?.mic),
        stockType = null
    )
}
