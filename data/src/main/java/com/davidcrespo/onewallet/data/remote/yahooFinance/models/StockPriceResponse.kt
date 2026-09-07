package com.davidcrespo.onewallet.data.remote.yahooFinance.models

import com.davidcrespo.onewallet.data.remote.dto.CurrencyDto
import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StockPriceResponse(
    @SerialName("chart") val chart: StockPriceResponseChart? = null
)

@Serializable
data class StockPriceResponseChart(
    @SerialName("result") val result: List<StockPriceResponseResult>? = null,
    @SerialName("error") val error: String? = null
)

@Serializable
data class StockPriceResponseResult(
    @SerialName("meta") val meta: StockPriceResponseMeta? = null
)

@Serializable
data class StockPriceResponseMeta(
    @SerialName("currency")
    val currency: String? = null,

    @SerialName("symbol")
    val symbol: String? = null,

    @SerialName("exchangeName")
    val exchangeName: String? = null,

    @SerialName("fullExchangeName")
    val fullExchangeName: String? = null,

    @SerialName("instrumentType")
    val instrumentType: String? = null,

    @SerialName("regularMarketPrice")
    val regularMarketPrice: Double? = null,

    @SerialName("chartPreviousClose")
    val chartPreviousClose: Double? = null,

    @SerialName("regularMarketTime")
    val regularMarketTime: Long? = null,

    @SerialName("exchangeTimezoneName")
    val exchangeTimezoneName: String? = null,

    @SerialName("shortName")
    val shortName: String? = null
)

fun StockPriceResponseMeta.toInvestDto(symbol: String, name: String) = InvestmentDto(
    symbol = symbol,
    name = name,
    quantity = 0.0,
    price = regularMarketPrice ?: 0.0,
    previousPrice = chartPreviousClose ?: 0.0,
    currency = CurrencyDto(currency ?: USD),
    type = InvestmentType.STOCK
)
