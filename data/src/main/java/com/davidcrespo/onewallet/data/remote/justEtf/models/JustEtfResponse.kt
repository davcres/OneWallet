package com.davidcrespo.onewallet.data.remote.justEtf.models

import com.davidcrespo.onewallet.data.remote.dto.CurrencyDto
import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import kotlinx.serialization.Serializable

@Serializable
data class JustEtfResponse(
    val latestQuote: QuoteValue?,
    val previousQuote: QuoteValue?,
)

@Serializable
data class QuoteValue(
    val raw: Double?,
    val localized: String?
)

fun JustEtfResponse.toInvestDto(isin: String, currency: CurrencyDto): InvestmentDto {
    return InvestmentDto(
        symbol = isin,
        name = "",
        quantity = 0.0,
        price = latestQuote?.raw ?: 0.0,
        previousPrice = previousQuote?.raw ?: 0.0,
        currency = currency,
        type = InvestmentType.ETF
    )
}
