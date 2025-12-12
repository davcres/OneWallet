package com.davidcrespo.onewallet.presentation.contract

import com.davidcrespo.onewallet.data.local.database.dao.MonthlyBalance
import com.davidcrespo.onewallet.data.local.database.entities.MonthlyPortfolioSnapshotEntity

data class HistoryUiState(
    val history: List<MonthlyBalance> = emptyList(),
    val selectedMonthDetail: List<MonthlyPortfolioSnapshotEntity>? = null,
    val isLoading: Boolean = false
)

sealed interface HistoryIntent {
    data class SelectMonth(val year: Int, val month: Int) : HistoryIntent
    data object DismissDetail : HistoryIntent
    data object NavigateBack : HistoryIntent
}
