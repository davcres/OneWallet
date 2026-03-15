package com.davidcrespo.onewallet.presentation.history

import androidx.compose.runtime.Immutable
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.presentation.models.CurrencyView
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class HistoryUiState(
    val history: ImmutableList<ImmutableList<InvestmentView>> = persistentListOf(),
    val selectedMonthDetail: ImmutableList<InvestmentView>? = null,
    val selectedPreviousMonth: ImmutableList<InvestmentView>? = null,
    val selectedInvestment: InvestmentView? = null,
    val selectedPreviousInvestment: InvestmentView? = null,
    val selectedCurrency: CurrencyView = CurrencyView.get(EUR),
    val isLoading: Boolean = false
)

sealed interface HistoryIntent {
    data object LoadInitialData : HistoryIntent
    data class SelectMonth(val year: Int, val month: Int) : HistoryIntent
    data class SelectInvestment(val investment: InvestmentView) : HistoryIntent
    data object DismissBottomSheet : HistoryIntent
    data object DismissInvestmentDetail : HistoryIntent
}
