package com.davidcrespo.onewallet.presentation.historical

import androidx.compose.runtime.Immutable
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.presentation.models.CurrencyView
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class HistoricalUiState(
    val history: ImmutableList<ImmutableList<InvestmentView>> = persistentListOf(),
    val selectedMonthDetail: ImmutableList<InvestmentView>? = null,
    val selectedPreviousMonth: ImmutableList<InvestmentView>? = null,
    val selectedInvestment: InvestmentView? = null,
    val selectedPreviousInvestment: InvestmentView? = null,
    val selectedCurrency: CurrencyView = CurrencyView.get(EUR),
    val isLoading: Boolean = false
)

sealed interface HistoricalIntent {
    data object LoadInitialData : HistoricalIntent
    data class SelectMonth(val year: Int, val month: Int) : HistoricalIntent
    data class SelectInvestment(val investment: InvestmentView) : HistoricalIntent
    data object DismissBottomSheet : HistoricalIntent
    data object DismissInvestmentDetail : HistoricalIntent
}
