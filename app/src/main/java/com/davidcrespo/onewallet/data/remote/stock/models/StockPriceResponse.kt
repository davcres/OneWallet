package com.davidcrespo.onewallet.data.remote.stock.models

import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import kotlinx.serialization.Serializable

@Serializable
data class StockPriceResponse(
    val c: Double, // current price
    val d: Double? = null, // change (cambio respecto al cierre anterior)
    val dp: Double? = null, // percent change (% cambio)
    val h: Double? = null, // high (máximo del día)
    val l: Double? = null, // low (mínimo del día)
    val o: Double? = null, // open price (apertura)
    val pc: Double? = null, // previous close (cierre previo)
    val t: Long? = null // timestamp Unix
)

fun StockPriceResponse.toInvestDto(symbol: String) = InvestmentDto(
    symbol = symbol,
    quantity = 0.0,
    price = c,
    previousPrice = pc ?: 0.0,
    currency = Currency.USD,
    type = InvestmentType.STOCK,
    year = 0,
    month = 0
)
