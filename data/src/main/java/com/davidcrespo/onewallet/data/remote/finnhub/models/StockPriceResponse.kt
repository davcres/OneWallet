package com.davidcrespo.onewallet.data.remote.finnhub.models

import com.davidcrespo.onewallet.data.remote.dto.CurrencyDto
import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
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

fun StockPriceResponse.toInvestDto(symbol: String, name: String) = InvestmentDto(
    symbol = symbol,
    name = name,
    quantity = 0.0,
    price = c,
    previousPrice = pc ?: 0.0,
    currency = CurrencyDto(USD),
    type = InvestmentType.STOCK
)
