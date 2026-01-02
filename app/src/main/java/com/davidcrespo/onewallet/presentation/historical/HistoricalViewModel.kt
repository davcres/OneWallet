package com.davidcrespo.onewallet.presentation.historical

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import com.davidcrespo.onewallet.domain.usecase.historical.GetMonthlyHistoryUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetUsdEurUseCase
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import com.davidcrespo.onewallet.presentation.models.toUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoricalViewModel(
    private val getMonthlyHistoryUseCase: GetMonthlyHistoryUseCase,
    private val financialRepository: FinancialRepository,
    private val getUsdEurUseCase: GetUsdEurUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoricalUiState())
    val uiState = _uiState.asStateFlow()

    fun handleIntent(intent: HistoricalIntent) {
        when (intent) {
            is HistoricalIntent.LoadInitialData -> loadInitialData()
            is HistoricalIntent.SelectMonth -> selectMonth(intent.year, intent.month)
            is HistoricalIntent.SelectInvestment -> selectInvestment(intent.investment)
            is HistoricalIntent.DismissBottomSheet -> dismissBottomSheet()
            is HistoricalIntent.DismissInvestmentDetail -> dismissInvestmentDetail()
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getSelectedCurrency()
            getUsdEurRate()
            getMonthlyHistory()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun getMonthlyHistory() {
        getMonthlyHistoryUseCase()
            .onSuccess { historyList ->
                val grouped: List<List<InvestmentView>> =
                    historyList
                        .map { it.toUI() }
                        .groupBy { it.year to it.month }
                        .values
                        .toList()

                _uiState.update {
                    it.copy(
                        history = grouped,
                    )
                }
            }
            .onFailure {
                _uiState.update {
                    it.copy(
                        history = emptyList(),
                    )
                }
            }
    }

    private fun selectMonth(year: Int, month: Int) {
        viewModelScope.launch {
            val details = _uiState.value.history.firstOrNull { monthlyEntries ->
                monthlyEntries.firstOrNull()?.let { it.year == year && it.month == month } == true
            }.orEmpty()

            val index = _uiState.value.history.indexOf(details)

            _uiState.update {
                it.copy(
                    selectedMonthDetail = details.sortedByDescending { it.quantity * it.displayPrice },
                    selectedPreviousMonth = _uiState.value.history.getOrNull(index + 1)
                )
            }
        }
    }

    private fun selectInvestment(investment: InvestmentView) {
        _uiState.update {
            it.copy(
                selectedInvestment = investment,
                selectedPreviousInvestment = it.selectedPreviousMonth?.find { it.symbol == investment.symbol }
            )
        }
    }

    private fun dismissBottomSheet() {
        _uiState.update {
            it.copy(
                selectedMonthDetail = null,
                selectedPreviousMonth = null
            )
        }
    }

    private fun dismissInvestmentDetail() {
        _uiState.update {
            it.copy(
                selectedInvestment = null,
                selectedPreviousInvestment = null
            )
        }
    }

    private fun getSelectedCurrency() {
        val selectedCurrency = financialRepository.getSelectedCurrency()
        _uiState.update {
            it.copy(
                selectedCurrency = selectedCurrency
            )
        }
    }

    private suspend fun getUsdEurRate() {
        getUsdEurUseCase()
            .onSuccess { rate ->
                _uiState.update { it.copy(usdEurRate = rate) }
            }.onFailure {
                _uiState.update { it.copy(usdEurRate = 1.0) }
            }
    }
}