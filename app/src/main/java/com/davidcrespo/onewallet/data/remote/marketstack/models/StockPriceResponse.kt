package com.davidcrespo.onewallet.data.remote.marketstack.models

import com.davidcrespo.onewallet.data.remote.dto.CurrencyDto
import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.UNKNOWN
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StockPriceResponseList(
    @SerialName("pagination")
    val pagination: Pagination,

    @SerialName("data")
    val data: List<StockPriceResponse>
)

@Serializable
data class StockPriceResponse(
    @SerialName("open")
    val open: Double,

    @SerialName("high")
    val high: Double,

    @SerialName("low")
    val low: Double,

    @SerialName("close")
    val close: Double,

    @SerialName("volume")
    val volume: Double,

    @SerialName("adj_high")
    val adjHigh: Double?,

    @SerialName("adj_low")
    val adjLow: Double?,

    @SerialName("adj_close")
    val adjClose: Double?,

    @SerialName("adj_open")
    val adjOpen: Double?,

    @SerialName("adj_volume")
    val adjVolume: Double?,

    @SerialName("split_factor")
    val splitFactor: Double,

    @SerialName("dividend")
    val dividend: Double,

    @SerialName("name")
    val name: String?,

    @SerialName("exchange_code")
    val exchangeCode: String?,

    @SerialName("asset_type")
    val assetType: String?,

    @SerialName("price_currency")
    val priceCurrency: String?,

    @SerialName("symbol")
    val symbol: String,

    @SerialName("exchange")
    val exchange: String,

    @SerialName("date")
    val date: String
)

fun StockPriceResponseList.toInvestDto(symbol: String, name: String) = InvestmentDto(
    symbol = symbol,
    name = name,
    quantity = 0.0,
    price = data.firstOrNull()?.close ?: 0.0,
    previousPrice = data.lastOrNull()?.close ?: 0.0,
    currency = data.firstOrNull()?.priceCurrency?.let { CurrencyDto(it) } ?: CurrencyDto(UNKNOWN),
    type = InvestmentType.STOCK,
    year = 0,
    month = 0
)
