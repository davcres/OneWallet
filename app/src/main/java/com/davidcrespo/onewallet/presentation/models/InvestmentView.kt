package com.davidcrespo.onewallet.presentation.models

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType

data class InvestmentView(
    val symbol: String,
    val name: String,
    val quantity: Double,
    val displayPrice: Double,
    val displayPreviousPrice: Double,
    val originalPrice: Double,
    val originalPreviousPrice: Double,
    val originalCurrency: Currency,
    val changePercent: Double,
    val type: InvestmentType,
    val month: Int,
    val year: Int
)

fun Investment.toUI(): InvestmentView {
    val changePercent = previousPrice
        .takeIf { it != 0.0 }
        ?.let { ((price - it) / it) * 100.0 } ?: 0.0

    return InvestmentView(
        symbol = symbol,
        name = name,
        quantity = quantity,
        displayPrice = price,
        displayPreviousPrice = previousPrice,
        originalPrice = price,
        originalPreviousPrice = previousPrice,
        originalCurrency = currency,
        changePercent = changePercent,
        type = type,
        month = month,
        year = year
    )
}

fun InvestmentView.toDomain(): Investment {
    return Investment(
        symbol = symbol,
        name = name,
        quantity = quantity,
        price = originalPrice,
        previousPrice = originalPreviousPrice,
        currency = originalCurrency,
        type = type,
        year = year,
        month = month
    )
}
