package com.davidcrespo.onewallet.data.remote.yahooFinance.models

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.GlobalMarketRegion
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.UNKNOWN
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MarketStockResponseList(
    @SerialName("count") val count: Int? = null,
    @SerialName("quotes") val quotes: List<MarketStockResponse>? = null
)

@Serializable
data class MarketStockResponse(
    @SerialName("symbol")
    val symbol: String? = null,

    @SerialName("shortname")
    val shortname: String? = null,

    @SerialName("longname")
    val longname: String? = null,

    @SerialName("exchange")
    val exchange: String? = null,

    @SerialName("exchDisp")
    val exchDisp: String? = null,

    @SerialName("quoteType")
    val quoteType: String? = null,

    @SerialName("typeDisp")
    val typeDisp: String? = null,

    @SerialName("sector")
    val sector: String? = null,

    @SerialName("industry")
    val industry: String? = null,

    @SerialName("isYahooFinance")
    val isYahooFinance: Boolean? = false
)

fun MarketStockResponse.toDomain(): MarketAsset {
    return MarketAsset(
        symbol = symbol.orEmpty(),
        price = 0.0,
        currency = Currency(UNKNOWN),
        type = InvestmentType.STOCK,
        description = longname,
        region = GlobalMarketRegion.from(exchange),
        stockType = quoteType
    )
}
