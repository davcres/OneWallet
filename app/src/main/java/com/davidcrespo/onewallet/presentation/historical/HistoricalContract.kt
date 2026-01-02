package com.davidcrespo.onewallet.presentation.historical

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.presentation.models.InvestmentView

data class HistoricalUiState(
    val history: List<List<InvestmentView>> = emptyList(),
    val selectedMonthDetail: List<InvestmentView>? = null,
    val selectedPreviousMonth: List<InvestmentView>? = null,
    val selectedInvestment: InvestmentView? = null,
    val selectedPreviousInvestment: InvestmentView? = null,
    val selectedCurrency: Currency = Currency.EUR,
    val usdEurRate: Double = 1.0,
    val isLoading: Boolean = false
)

sealed interface HistoricalIntent {
    data object LoadInitialData : HistoricalIntent
    data class SelectMonth(val year: Int, val month: Int) : HistoricalIntent
    data class SelectInvestment(val investment: InvestmentView) : HistoricalIntent
    data object DismissBottomSheet : HistoricalIntent
    data object DismissInvestmentDetail : HistoricalIntent
}
