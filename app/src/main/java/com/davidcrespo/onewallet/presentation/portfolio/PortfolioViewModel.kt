package com.davidcrespo.onewallet.presentation.portfolio

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.usecase.portfolio.AddInvestmentToPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetInvestmentPriceUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetPortfolioItemsUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetUsdEurUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.RemovePortfolioItemUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.SaveMonthlyPortfolioUseCase
import com.davidcrespo.onewallet.widget.portfolio.PortfolioWidget
import com.davidcrespo.onewallet.widget.stocks.StocksWidget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class PortfolioViewModel(
    private val getUsdEurUseCase: GetUsdEurUseCase,
    private val getPortfolioItemsUseCase: GetPortfolioItemsUseCase,
    private val getInvestmentPriceUseCase: GetInvestmentPriceUseCase,
    private val saveMonthlyPortfolioUseCase: SaveMonthlyPortfolioUseCase,
    private val addInvestmentToPortfolioUseCase: AddInvestmentToPortfolioUseCase,
    private val removePortfolioItemUseCase: RemovePortfolioItemUseCase,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PortfolioUiState())
    val uiState = _uiState.asStateFlow()

    fun handleIntent(intent: PortfolioIntent) {
        when (intent) {
            is PortfolioIntent.LoadInitialData -> loadInitialData()
            is PortfolioIntent.UpdateBalance -> setTotalBalance()

            is PortfolioIntent.EditQuantity -> _uiState.update { it.copy(editingItem = intent.item) }
            is PortfolioIntent.UpdateQuantity -> updateQuantity(intent.item, intent.quantity)
            is PortfolioIntent.RemoveItem -> removeItem(intent.item)

            is PortfolioIntent.AddFundItem -> addFundItem(intent.name, intent.quantity, intent.price)
            is PortfolioIntent.ShowFundDialog -> _uiState.update { it.copy(isFundDialogVisible = true) }
            is PortfolioIntent.DismissFundDialog -> _uiState.update { it.copy(isFundDialogVisible = false) }

            is PortfolioIntent.AddBankItem -> addBankItem(intent.name, intent.quantity)
            is PortfolioIntent.ShowBankDialog -> _uiState.update { it.copy(isBankDialogVisible = true) }
            is PortfolioIntent.DismissBankDialog -> _uiState.update { it.copy(isBankDialogVisible = false) }
        }
    }

    private fun loadInitialData() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getUsdEurRate()
            getPortfolioItems()
            sortPortfolioItems()
            setTotalBalance()
            savePortfolio()
            updateWidgets()
        }
        _uiState.update { it.copy(isLoading = false) }
    }

    private suspend fun getUsdEurRate() {
        getUsdEurUseCase()
            .onSuccess { rate ->
                _uiState.update { it.copy(usdEurRate = rate) }
            }.onFailure {
                _uiState.update { it.copy(usdEurRate = 1.0) }
            }
    }

    private suspend fun getPortfolioItems() {
        getPortfolioItemsUseCase()
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
            .collect { items ->
                _uiState.update { it.copy(portfolioItems = items.toMutableList()) }
                fetchPricesForItems(items)
            }
    }

    private fun fetchPricesForItems(items: List<Investment>) {
        viewModelScope.launch {
            val symbolsWithPrice = uiState.value.symbolsWithPrice

            val symbolsToFetch = items
                .filter { it.type == InvestmentType.STOCK || it.type == InvestmentType.CRYPTO }
                .distinctBy { it.symbol }

            symbolsToFetch.forEach { item ->
                val symbol = item.symbol
                if (symbolsWithPrice.contains(symbol)) return@forEach

                launch {
                    getInvestmentPriceUseCase(symbol, item.type)
                        .onSuccess { investment ->
                            val portfolioItems = _uiState.value.portfolioItems
                            portfolioItems.remove(item)
                            if (investment.currency == Currency.EUR) {
                                portfolioItems.add(investment)
                            } else {
                                val newPrice = investment.price * _uiState.value.usdEurRate
                                portfolioItems.add(investment.setNewPrice(newPrice))
                            }
                            _uiState.update {
                                it.copy(
                                    portfolioItems = portfolioItems,
                                    symbolsWithPrice = uiState.value.symbolsWithPrice + investment.symbol
                                )
                            }
                        }
                }
            }
        }
    }

    private fun sortPortfolioItems() {
        _uiState.update {
            it.copy(
                portfolioItems = _uiState.value.portfolioItems.sortedByDescending {
                    it.quantity * it.price
                }.toMutableList()
            )
        }
    }

    private fun setTotalBalance() {
        val totalBalance = _uiState.value.portfolioItems.sumOf {
            it.quantity * it.price
        }

        _uiState.update {
            it.copy(
                totalBalance = totalBalance
            )
        }
    }

    private suspend fun savePortfolio() {
        saveMonthlyPortfolioUseCase(_uiState.value.portfolioItems)
    }

    private suspend fun updateWidgets() {
        PortfolioWidget().updateAll(context)
        StocksWidget().updateAll(context)
    }

    private fun addFundItem(name: String, quantity: Double, price: Double) {
        viewModelScope.launch {
            val now = LocalDate.now()
            val year = now.year
            val month = now.monthValue

            val fund = Investment(
                symbol = name,
                quantity = quantity,
                price = price,
                previousPrice = 0.0,
                currency = Currency.EUR,
                type = InvestmentType.FUND,
                year = year,
                month = month
            )

            addInvestmentToPortfolioUseCase(fund)
            _uiState.update { it.copy(isFundDialogVisible = false) }
        }
    }
    private fun addBankItem(name: String, quantity: Double) {
        viewModelScope.launch {
            val now = LocalDate.now()
            val year = now.year
            val month = now.monthValue

            val cash = Investment(
                symbol = name,
                quantity = quantity,
                price = 1.0,
                previousPrice = 0.0,
                currency = Currency.EUR,
                type = InvestmentType.CASH,
                year = year,
                month = month
            )

            addInvestmentToPortfolioUseCase(cash)
            _uiState.update { it.copy(isBankDialogVisible = false) }
        }
    }

    private fun removeItem(item: Investment) {
        viewModelScope.launch {
            removePortfolioItemUseCase(item)
        }
    }

    private fun updateQuantity(item: Investment, quantity: Double) {
        viewModelScope.launch {
            val now = LocalDate.now()
            val year = now.year
            val month = now.monthValue

            val itemUpdated = item.copy(
                quantity = quantity,
                year = year,
                month = month
            )
            addInvestmentToPortfolioUseCase(itemUpdated)

            _uiState.update { it.copy(editingItem = null) }
        }
    }
}