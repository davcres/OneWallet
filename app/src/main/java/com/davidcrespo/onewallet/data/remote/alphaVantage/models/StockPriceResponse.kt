package com.davidcrespo.onewallet.data.remote.alphaVantage.models

import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StockPriceResponseObject(
    @SerialName("Global Quote")
    val globalQuote: StockPriceResponse
)

@Serializable
data class StockPriceResponse(
    @SerialName("01. symbol")
    val symbol: String,

    @SerialName("02. open")
    val open: String,

    @SerialName("03. high")
    val high: String,

    @SerialName("04. low")
    val low: String,

    @SerialName("05. price")
    val price: String,

    @SerialName("06. volume")
    val volume: String,

    @SerialName("07. latest trading day")
    val latestTradingDay: String,

    @SerialName("08. previous close")
    val previousClose: String,

    @SerialName("09. change")
    val change: String,

    @SerialName("10. change percent")
    val changePercent: String
)

fun StockPriceResponse.toInvestDto(symbol: String, name: String, currency: Currency) = InvestmentDto(
    symbol = symbol,
    name = name,
    quantity = 0.0,
    price = price.toDoubleOrNull() ?: 0.0,
    previousPrice = previousClose.toDoubleOrNull() ?: 0.0,
    currency = currency,
    type = InvestmentType.STOCK,
    year = 0,
    month = 0
)
