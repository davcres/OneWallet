package com.davidcrespo.onewallet.presentation.portfolio

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetUsdEurUseCase
import com.davidcrespo.onewallet.presentation.models.InvestmentView

class CurrencyConverter(
    private val getUsdEurUseCase: GetUsdEurUseCase
) {

    suspend fun convert(
        investment: InvestmentView,
        to: Currency
    ): InvestmentView {
        val from = investment.originalCurrency
        if (from == to) {
            return investment.copy(
                displayPrice = investment.originalPrice,
                displayPreviousPrice = investment.originalPreviousPrice
            )
        }

        val rateEurPerUsd = getUsdEurUseCase().getOrNull() ?: return investment

        val factor = when {
            from == Currency.USD && to == Currency.EUR -> rateEurPerUsd
            from == Currency.EUR && to == Currency.USD -> 1.0 / rateEurPerUsd
            else -> 1.0
        }

        return investment.copy(
            displayPrice = investment.originalPrice * factor,
            displayPreviousPrice = investment.originalPreviousPrice * factor
        )
    }

}