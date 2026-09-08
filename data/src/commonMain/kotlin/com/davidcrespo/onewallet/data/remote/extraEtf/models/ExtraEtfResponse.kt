package com.davidcrespo.onewallet.data.remote.extraEtf.models

import com.davidcrespo.onewallet.data.remote.dto.CurrencyDto
import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.UNKNOWN
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExtraEtfResponse(
    val results: List<AssetResult>?
)

@Serializable
data class AssetResult(
    val isin: String?,
    val fondName: String?,
    val currency: String?,
    val nav: Double?,
    @SerialName("nav_date") val navDate: String?,
    @SerialName("last_quote") val lastQuote: LastQuote?,
    @SerialName("returns") val returns: Map<String, ReturnDetail>?,
)

@Serializable
data class LastQuote(
    @SerialName("a") val ask: Double?,
    @SerialName("b") val bid: Double?,
    @SerialName("m") val mid: Double?,
    @SerialName("c") val currency: String?
)

@Serializable
data class ReturnDetail(
    @SerialName("close_price") val closePrice: Double?,
    @SerialName("performance_currency") val currency: String?,
    @SerialName("price_date") val priceDate: String?
)

fun ExtraEtfResponse.toInvestDto(): InvestmentDto {
    val etf = results?.firstOrNull()

    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val yesterday = today.minus(1, DateTimeUnit.DAY)
    val previousDay = etf?.returns?.values?.find {
        it.closePrice != null && runCatching { it.priceDate?.let { dateStr -> LocalDate.parse(dateStr) == yesterday } }.getOrDefault(false) == true
    }
    val currency = etf?.lastQuote?.currency ?: etf?.currency ?: previousDay?.currency

    return InvestmentDto(
        symbol = etf?.isin.orEmpty(),
        name = etf?.fondName.orEmpty(),
        quantity = 0.0,
        price = etf?.lastQuote?.mid ?: etf?.nav ?: 0.0,
        previousPrice = previousDay?.closePrice ?: 0.0,
        currency = if (!currency.isNullOrEmpty()) CurrencyDto(currency) else CurrencyDto(UNKNOWN),
        type = InvestmentType.ETF
    )
}
