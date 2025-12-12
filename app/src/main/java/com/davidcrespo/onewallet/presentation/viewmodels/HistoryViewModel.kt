package com.davidcrespo.onewallet.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetMonthlyDetailUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetMonthlyHistoryUseCase
import com.davidcrespo.onewallet.presentation.contract.HistoryIntent
import com.davidcrespo.onewallet.presentation.contract.HistoryUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val getMonthlyHistoryUseCase: GetMonthlyHistoryUseCase,
    private val getMonthlyDetailUseCase: GetMonthlyDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getMonthlyHistoryUseCase().collect { historyList ->
                _uiState.update { 
                    it.copy(
                        history = historyList,
                        isLoading = false
                    ) 
                }
            }
        }
    }

    fun handleIntent(intent: HistoryIntent) {
        when (intent) {
            is HistoryIntent.SelectMonth -> selectMonth(intent.year, intent.month)
            is HistoryIntent.DismissDetail -> dismissDetail()
            is HistoryIntent.NavigateBack -> { /* Handled by UI callback, but good to have in contract */ }
        }
    }

    private fun selectMonth(year: Int, month: Int) {
        viewModelScope.launch {
            val details = getMonthlyDetailUseCase(year, month)
            _uiState.update { it.copy(selectedMonthDetail = details) }
        }
    }

    private fun dismissDetail() {
        _uiState.update { it.copy(selectedMonthDetail = null) }
    }
}
