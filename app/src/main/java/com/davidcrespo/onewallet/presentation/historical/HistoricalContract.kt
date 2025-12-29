package com.davidcrespo.onewallet.presentation.historical

import com.davidcrespo.onewallet.domain.model.investment.Investment

data class HistoricalUiState(
    val history: List<List<Investment>> = emptyList(),
    val selectedMonthDetail: List<Investment>? = null,
    val selectedPreviousMonth: List<Investment>? = null,
    val selectedInvestment: Investment? = null,
    val selectedPreviousInvestment: Investment? = null,
    val isLoading: Boolean = false
)

sealed interface HistoricalIntent {
    data object LoadInitialData : HistoricalIntent
    data class SelectMonth(val year: Int, val month: Int) : HistoricalIntent
    data class SelectInvestment(val investment: Investment) : HistoricalIntent
    data object DismissBottomSheet : HistoricalIntent
    data object DismissInvestmentDetail : HistoricalIntent
}
