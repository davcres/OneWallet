package com.davidcrespo.onewallet.presentation.portfolio

import android.content.Context
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
import com.davidcrespo.onewallet.widget.WidgetsRefreshWorker
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getUsdEurRate()
            getPortfolioItems()
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
                sortPortfolioItems()
                updateWidgets()
                
                _uiState.update { it.copy(isLoading = false) }
            }
    }

    private fun fetchPricesForItems(items: List<Investment>) {
        viewModelScope.launch {
            // Stable shot of initial state (avoid read _uiState in concurrent coroutines)
            val currentUsdEurRate = _uiState.value.usdEurRate
            val alreadyPricedSymbols = _uiState.value.symbolsWithPrice.toSet()

            // Keep Funds/Cash
            val fixedItems = items
                .filter { it.type == InvestmentType.FUND || it.type == InvestmentType.CASH }
                .distinctBy { it.symbol }

            // Stocks/Crypto to update
            val marketItems = items
                .filter { it.type == InvestmentType.STOCK || it.type == InvestmentType.CRYPTO }
                .distinctBy { it.symbol }

            // Concurrent fetch
            val updatedMarketItems: List<Investment> = coroutineScope {
                marketItems.map { item ->
                    async {
                        val symbol = item.symbol

                        if (symbol in alreadyPricedSymbols) return@async item

                        getInvestmentPriceUseCase(symbol, item.type)
                            .fold(
                                onSuccess = { investmentFromApi ->
                                    val newPrice = if (investmentFromApi.currency == Currency.EUR) {
                                        investmentFromApi.price
                                    } else {
                                        investmentFromApi.price * currentUsdEurRate
                                    }

                                    item.copy(
                                        price = newPrice
                                    )
                                },
                                onFailure = {
                                    item
                                }
                            )
                    }
                }.awaitAll()
            }

            // Final list to save
            val symbolsToSave: List<Investment> = (fixedItems + updatedMarketItems).distinctBy { it.symbol }

            // Update symbolsWithPrice
            val newSymbolsWithPrice = (alreadyPricedSymbols + updatedMarketItems.map { it.symbol }).toList()

            _uiState.update {
                it.copy(
                    portfolioItems = symbolsToSave.toMutableList(),
                    symbolsWithPrice = newSymbolsWithPrice
                )
            }

            if (updatedMarketItems.any { it.symbol !in alreadyPricedSymbols }) {
                savePortfolio(symbolsToSave)
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

    private suspend fun savePortfolio(items: List<Investment>) {
        saveMonthlyPortfolioUseCase(items)
    }

    private fun updateWidgets() {
        viewModelScope.launch {
            WidgetsRefreshWorker.enqueue(context)
        }
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