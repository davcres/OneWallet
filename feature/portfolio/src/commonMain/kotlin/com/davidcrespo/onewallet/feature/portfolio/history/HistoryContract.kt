package com.davidcrespo.onewallet.feature.portfolio.history

import androidx.compose.runtime.Immutable
import com.davidcrespo.onewallet.core.models.CurrencyView
import com.davidcrespo.onewallet.core.models.InvestmentView
import com.davidcrespo.onewallet.domain.model.investment.EUR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.StringResource

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

sealed interface HistoryEffect {
    data object ShowFilePicker : HistoryEffect
    data class ShowSnackbar(val message: StringResource) : HistoryEffect
}

sealed interface HistoryIntent {
    data object LoadInitialData : HistoryIntent
    data object OnCurrencyChanged : HistoryIntent
    data class SelectMonth(val year: Int, val month: Int) : HistoryIntent
    data class SelectInvestment(val investment: InvestmentView) : HistoryIntent
    data object DismissBottomSheet : HistoryIntent
    data object DismissInvestmentDetail : HistoryIntent
    data object ImportHistory : HistoryIntent
    data object ExportHistory : HistoryIntent
    data class OnFileSelected(val uri: String) : HistoryIntent
}
