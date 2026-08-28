package com.davidcrespo.onewallet.feature.portfolio.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidcrespo.onewallet.core.extensions.orEmpty
import com.davidcrespo.onewallet.domain.repository.FileRepository
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import com.davidcrespo.onewallet.domain.usecase.history.ExportHistoryUseCase
import com.davidcrespo.onewallet.domain.usecase.history.GetMonthlyHistoryUseCase
import com.davidcrespo.onewallet.domain.usecase.history.ImportHistoryUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetCurrencyRateUseCase
import com.davidcrespo.onewallet.core.models.InvestmentView
import com.davidcrespo.onewallet.core.models.toUI
import com.davidcrespo.onewallet.domain.usecase.portfolio.CurrencyConverter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val getMonthlyHistoryUseCase: GetMonthlyHistoryUseCase,
    private val financialRepository: FinancialRepository,
    private val getCurrencyRateUseCase: GetCurrencyRateUseCase,
    private val currencyConverter: CurrencyConverter,
    private val importHistoryUseCase: ImportHistoryUseCase,
    private val exportHistoryUseCase: ExportHistoryUseCase,
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<HistoryEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: HistoryIntent) {
        when (intent) {
            is HistoryIntent.LoadInitialData -> loadInitialData()
            is HistoryIntent.OnCurrencyChanged -> {
                getSelectedCurrency()
                onCurrencyChanged()
            }
            is HistoryIntent.SelectMonth -> selectMonth(intent.year, intent.month)
            is HistoryIntent.SelectInvestment -> selectInvestment(intent.investment)
            is HistoryIntent.DismissBottomSheet -> dismissBottomSheet()
            is HistoryIntent.DismissInvestmentDetail -> dismissInvestmentDetail()
            is HistoryIntent.ImportHistory -> {
                viewModelScope.launch {
                    _effect.send(HistoryEffect.ShowFilePicker)
                }
            }
            is HistoryIntent.ExportHistory -> exportHistory()
            is HistoryIntent.OnFileSelected -> importHistory(intent.uri)
        }
    }

    private fun importHistory(uri: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            fileRepository.readFromUri(uri).onSuccess { content ->
                importHistoryUseCase(content).onSuccess {
                    getMonthlyHistory()
                    _uiState.update { it.copy(isLoading = false) }
                    _effect.send(HistoryEffect.ShowSnackbar(com.davidcrespo.onewallet.core.R.string.history_import_success))
                }.onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                    _effect.send(HistoryEffect.ShowSnackbar(com.davidcrespo.onewallet.core.R.string.history_import_error))
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
                _effect.send(HistoryEffect.ShowSnackbar(com.davidcrespo.onewallet.core.R.string.history_read_error))
            }
        }
    }

    private fun exportHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            exportHistoryUseCase().onSuccess { content ->
                fileRepository.saveToDownloads("onewallet_history.csv", content).onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _effect.send(HistoryEffect.ShowSnackbar(com.davidcrespo.onewallet.core.R.string.history_export_success))
                }.onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                    _effect.send(HistoryEffect.ShowSnackbar(com.davidcrespo.onewallet.core.R.string.history_save_error))
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
                _effect.send(HistoryEffect.ShowSnackbar(com.davidcrespo.onewallet.core.R.string.history_export_error))
            }
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

    private fun onCurrencyChanged() {
        viewModelScope.launch {
            val state = _uiState.value
            val newSelectedCurrency = state.selectedCurrency
            val history = state.history

            val historyConverted = history.map { monthlyList ->
                monthlyList.map { investment ->
                    val rate = getCurrencyRateUseCase(
                        from = investment.originalCurrency.code,
                        to = newSelectedCurrency.code
                    ).fold(
                        onSuccess = { it },
                        onFailure = { 1.0 }
                    )

                    val priceConverted = currencyConverter.convert(
                        amount = investment.originalPrice,
                        from = investment.originalCurrency.code,
                        to = newSelectedCurrency.code,
                        rate = rate
                    )

                    val previousPriceConverted = currencyConverter.convert(
                        amount = investment.originalPreviousPrice,
                        from = investment.originalCurrency.code,
                        to = newSelectedCurrency.code,
                        rate = rate
                    )

                    investment.copy(
                        displayPrice = priceConverted,
                        displayPreviousPrice = previousPriceConverted
                    )
                }.toImmutableList()
            }.toImmutableList()

            _uiState.update { state ->
                val selectedMonthDetailConverted = state.selectedMonthDetail?.let { current ->
                    current.firstOrNull()?.let { first ->
                        historyConverted.firstOrNull { it.firstOrNull()?.let { m -> m.year == first.year && m.month == first.month } == true }
                            ?.sortedByDescending { it.quantity * it.displayPrice }?.toImmutableList()
                    }
                }

                val index = selectedMonthDetailConverted?.let { historyConverted.indexOf(it) } ?: -1
                val selectedPreviousMonthConverted = if (index != -1 && index + 1 < historyConverted.size) {
                    historyConverted[index + 1]
                } else null

                val selectedInvestmentConverted = state.selectedInvestment?.let { oldInv ->
                    selectedMonthDetailConverted?.find { it.symbol == oldInv.symbol }
                }

                val selectedPreviousInvestmentConverted = selectedInvestmentConverted?.let { newInv ->
                    selectedPreviousMonthConverted?.find { it.symbol == newInv.symbol }
                }

                state.copy(
                    history = historyConverted,
                    selectedMonthDetail = selectedMonthDetailConverted,
                    selectedPreviousMonth = selectedPreviousMonthConverted,
                    selectedInvestment = selectedInvestmentConverted,
                    selectedPreviousInvestment = selectedPreviousInvestmentConverted
                )
            }
        }
    }
}
