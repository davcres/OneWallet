package com.davidcrespo.onewallet.data.remote.finnhub.models

import com.davidcrespo.onewallet.domain.model.finnhub.Quote
import kotlinx.serialization.Serializable

@Serializable
data class QuoteResponse(
    val c: Double, // current price
    val d: Double? = null, // change (cambio respecto al cierre anterior)
    val dp: Double? = null, // percent change (% cambio)
    val h: Double? = null, // high (máximo del día)
    val l: Double? = null, // low (mínimo del día)
    val o: Double? = null, // open price (apertura)
    val pc: Double? = null, // previous close (cierre previo)
    val t: Long? = null // timestamp Unix
)

fun QuoteResponse.toDomain() = Quote(
    currentPrice = c,
    changePercent = dp
)
