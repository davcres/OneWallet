package com.davidcrespo.onewallet.presentation.historical

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.usecase.historical.GetMonthlyHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoricalViewModel(
    private val getMonthlyHistoryUseCase: GetMonthlyHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoricalUiState())
    val uiState = _uiState.asStateFlow()

    fun handleIntent(intent: HistoricalIntent) {
        when (intent) {
            is HistoricalIntent.LoadInitialData -> loadInitialData()
            is HistoricalIntent.SelectMonth -> selectMonth(intent.year, intent.month)
            is HistoricalIntent.DismissDetail -> dismissDetail()
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getMonthlyHistoryUseCase().collect { historyList ->
                val grouped: List<List<Investment>> =
                    historyList.groupBy { it.year to it.month }
                        .values
                        .toList()

                _uiState.update {
                    it.copy(
                        history = grouped,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun selectMonth(year: Int, month: Int) {
        viewModelScope.launch {
            val details = _uiState.value.history.firstOrNull { monthlyEntries ->
                monthlyEntries.firstOrNull()?.let { it.year == year && it.month == month } == true
            }.orEmpty()

            _uiState.update {
                it.copy(
                    selectedMonthDetail = details.sortedByDescending { it.quantity * it.price }
                )
            }
        }
    }

    private fun dismissDetail() {
        _uiState.update { it.copy(selectedMonthDetail = null) }
    }
}