package com.davidcrespo.onewallet.data.remote.etf.justEtf.models

import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JustEtfDetailResponse(
    val etfs: List<Etf>?
)

@Serializable
data class Etf(
    val name: String?,
    val isin: String?,
    @SerialName("quote") val previousQuote: ValueWithLocalized?,
    val latestQuote: ValueWithLocalized?,
)

@Serializable
data class ValueWithLocalized(
    val raw: Double?,
    val localized: String?
)

fun JustEtfDetailResponse.toInvestDto(currency: Currency): InvestmentDto {
    val etf = etfs?.firstOrNull()

    return InvestmentDto(
        symbol = etf?.isin.orEmpty(),
        name = etf?.name.orEmpty(),
        quantity = 0.0,
        price = etf?.latestQuote?.raw ?: 0.0,
        previousPrice = etf?.previousQuote?.raw ?: 0.0,
        currency = currency,
        type = InvestmentType.ETF,
        year = 0,
        month = 0
    )
}
