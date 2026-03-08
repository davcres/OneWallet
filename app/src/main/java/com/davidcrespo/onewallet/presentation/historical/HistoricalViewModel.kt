package com.davidcrespo.onewallet.presentation.historical

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidcrespo.onewallet.core.extensions.orEmpty
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import com.davidcrespo.onewallet.domain.usecase.historical.GetMonthlyHistoryUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetCurrencyRateUseCase
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import com.davidcrespo.onewallet.presentation.models.toUI
import com.davidcrespo.onewallet.presentation.portfolio.CurrencyConverter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoricalViewModel(
    private val getMonthlyHistoryUseCase: GetMonthlyHistoryUseCase,
    private val financialRepository: FinancialRepository,
    private val getCurrencyRateUseCase: GetCurrencyRateUseCase,
    private val currencyConverter: CurrencyConverter
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
            getMonthlyHistory()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun getMonthlyHistory() {
        val state = _uiState.value
        val selectedCurrency = state.selectedCurrency
        getMonthlyHistoryUseCase()
            .onSuccess { historyList ->
                val grouped: ImmutableList<ImmutableList<InvestmentView>> =
                    historyList
                        .map { it.toUI() }
                        .map { investment ->
                            val rate = getCurrencyRateUseCase(
                                from = investment.originalCurrency.code,
                                to = selectedCurrency.code
                            ).fold(
                                onSuccess = { it },
                                onFailure = { 1.0 }
                            )

                            val priceConverted = currencyConverter.convert(
                                amount = investment.originalPrice,
                                from = investment.originalCurrency.code,
                                to = selectedCurrency.code,
                                rate = rate
                            )

                            val previousPriceConverted = currencyConverter.convert(
                                amount = investment.originalPreviousPrice,
                                from = investment.originalCurrency.code,
                                to = selectedCurrency.code,
                                rate = rate
                            )

                            investment.copy(
                                displayPrice = priceConverted,
                                displayPreviousPrice = previousPriceConverted
                            )
                        }
                        .groupBy { it.year to it.month }
                        .values
                        .map { it.toImmutableList() }
                        .toImmutableList()


                _uiState.update {
                    it.copy(
                        history = grouped,
                    )
                }
            }
            .onFailure {
                _uiState.update {
                    it.copy(
                        history = persistentListOf(),
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
                    selectedMonthDetail = details.sortedByDescending { it.quantity * it.displayPrice }.toImmutableList(),
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
                selectedCurrency = selectedCurrency.toUI()
            )
        }
    }
}