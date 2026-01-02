package com.davidcrespo.onewallet.presentation.portfolio

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetUsdEurUseCase
import com.davidcrespo.onewallet.presentation.models.InvestmentView

class CurrencyConverter(
    private val getUsdEurUseCase: GetUsdEurUseCase
) {

    suspend fun convert(investment: InvestmentView, from: Currency, to: Currency): InvestmentView {
        val rate = getUsdEurUseCase().getOrDefault(1.0)

        val (convertedPrice, convertedPreviousPrice) = if (from == Currency.EUR && to == Currency.USD) {
            Pair(investment.originalPrice / rate, investment.displayPreviousPrice / rate)
        } else if (from == Currency.USD && to == Currency.EUR) {
            Pair(investment.originalPrice * rate, investment.displayPreviousPrice * rate)
        } else {
            Pair(investment.originalPrice, investment.displayPreviousPrice)
        }

        return investment.copy(
            displayPrice = convertedPrice,
            displayPreviousPrice = convertedPreviousPrice
        )
    }
}