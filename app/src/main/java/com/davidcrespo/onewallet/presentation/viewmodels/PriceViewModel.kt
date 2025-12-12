package com.davidcrespo.onewallet.presentation.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidcrespo.onewallet.domain.model.PortfolioItem
import com.davidcrespo.onewallet.domain.model.finnhub.StockInfo
import com.davidcrespo.onewallet.domain.usecase.GetPriceUseCase
import com.davidcrespo.onewallet.domain.usecase.GetQuoteUseCase
import com.davidcrespo.onewallet.domain.usecase.GetSymbolsUseCase
import com.davidcrespo.onewallet.domain.usecase.GetUsdEurUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.AddPortfolioItemUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetPortfolioItemsUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.RemovePortfolioItemUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.ReorderPortfolioItemsUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.SaveMonthlySnapshotUseCase
import com.davidcrespo.onewallet.presentation.contract.PriceIntent
import com.davidcrespo.onewallet.presentation.contract.PriceScreenType
import com.davidcrespo.onewallet.presentation.contract.PriceUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import java.util.UUID

class PriceViewModel(
    private val getPriceUseCase: GetPriceUseCase,
    private val getSymbolsUseCase: GetSymbolsUseCase,
    private val getQuoteUseCase: GetQuoteUseCase,
    private val getPortfolioItemsUseCase: GetPortfolioItemsUseCase,
    private val addPortfolioItemUseCase: AddPortfolioItemUseCase,
    private val reorderPortfolioItemsUseCase: ReorderPortfolioItemsUseCase,
    private val removePortfolioItemUseCase: RemovePortfolioItemUseCase,
    private val getUsdEurUseCase: GetUsdEurUseCase,
    private val saveMonthlySnapshotUseCase: SaveMonthlySnapshotUseCase,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(PriceUiState())
    val uiState = _uiState.asStateFlow()

    private val _prices = MutableStateFlow<Map<String, Double>>(emptyMap())
    private var _usdToEurRate: Double = 1.0

    private val PREF_CUSTOM_SORT = "is_custom_sort"

    init {
        viewModelScope.launch {
            combine(
                getPortfolioItemsUseCase(),
                _prices
            ) { items, prices ->
                items.map { item ->
                    if (item.stockInfo.type == "CASH") {
                        item.copy(currentPrice = 1.0)
                    } else {
                        // Prices in map are already converted to EUR
                        val convertedPrice = prices[item.stockInfo.displaySymbol]
                        item.copy(currentPrice = convertedPrice)
                    }
                }
            }.collect { mappedItems ->
                val totalValue = mappedItems.sumOf {
                    it.quantity * (it.currentPrice ?: 0.0)
                }

                // Check if user has manually reordered the list
                val isCustomSort = sharedPreferences.getBoolean(PREF_CUSTOM_SORT, false)

                val displayItems = if (isCustomSort) {
                    // Respect DB sort order (already sorted by repository)
                    mappedItems
                } else {
                    // Default: Sort by Total Value ($) Descending
                    mappedItems.sortedByDescending { it.quantity * (it.currentPrice ?: 0.0) }
                }

                _uiState.update {
                    it.copy(
                        portfolioItems = displayItems,
                        totalBalance = totalValue
                    )
                }

                // Save Snapshot if prices are available OR if list is empty (to clear snapshot)
                val hasPrices = mappedItems.any { (it.currentPrice ?: 0.0) > 0.0 }
                if (mappedItems.isEmpty() || hasPrices) {
                   saveMonthlySnapshotUseCase(mappedItems) 
                }

                fetchPricesForItems(mappedItems)
            }
        }
    }

    private fun fetchPricesForItems(items: List<PortfolioItem>) {
        val symbolsToFetch = items
            .filter { it.stockInfo.type != "CASH" }
            .map { it.stockInfo.displaySymbol }
            .distinct()

        viewModelScope.launch {
            val currentMap = _prices.value
            val missingSymbols = symbolsToFetch.filter { !currentMap.containsKey(it) }

            if (missingSymbols.isNotEmpty()) {
                missingSymbols.forEach { symbol ->
                    launch {
                        getQuoteUseCase(symbol).onSuccess { quote ->
                            // Convert price to EUR
                            // Assuming quote comes in USD for US stocks.
                            // Ideally, we check quote.currency or stockInfo.currency, but Finnhub free tier is limited.
                            // We applied getSymbols("US"), so likely USD.
                            val priceInEur = quote.currentPrice * _usdToEurRate
                            _prices.update { it + (symbol to priceInEur) }
                        }.onFailure {
                            // Handle error or ignore
                        }
                    }
                }
            }
        }
    }

    fun handleIntent(intent: PriceIntent) {
        when (intent) {
            is PriceIntent.LoadInitialData -> loadInitialData()
            is PriceIntent.SearchQueryChanged -> updateSearchQuery(intent.query)
            is PriceIntent.SelectSymbol -> selectSymbol(intent.symbol)
            is PriceIntent.MoveSymbol -> moveSymbol(intent.fromIndex, intent.toIndex)
            is PriceIntent.EditQuantity -> _uiState.update { it.copy(editingItem = intent.item) }
            is PriceIntent.UpdateQuantity -> updateQuantity(intent.item, intent.quantity)
            is PriceIntent.RemoveItem -> removeItem(intent.item)
            
            is PriceIntent.ShowBankDialog -> _uiState.update { it.copy(isBankDialogVisible = true) }
            is PriceIntent.DismissBankDialog -> _uiState.update { it.copy(isBankDialogVisible = false) }
            is PriceIntent.AddBankItem -> addBankItem(intent.name, intent.amount)

            is PriceIntent.NavigateToAddInvestment -> {
                _uiState.update { 
                    it.copy(
                        currentScreen = PriceScreenType.AddInvestment,
                        searchQuery = "",
                        filteredSymbols = it.symbols
                    ) 
                }
            }
            is PriceIntent.NavigateToHistory -> {
                _uiState.update {
                    it.copy(currentScreen = PriceScreenType.History)
                }
            }
            is PriceIntent.NavigateBack -> {
                _uiState.update { 
                    it.copy(
                        currentScreen = PriceScreenType.Portfolio,
                        searchQuery = ""
                    ) 
                }
            }
        }
    }

    private fun addBankItem(name: String, amount: Double) {
        viewModelScope.launch {
            val bankStockInfo = StockInfo(
                description = "Efectivo / Banco",
                displaySymbol = name,
                currency = "EUR",
                type = "CASH",
                figi = "CASH_${UUID.randomUUID()}",
                isin = ""
            )
            
            addPortfolioItemUseCase(bankStockInfo, amount)
            _uiState.update { it.copy(isBankDialogVisible = false) }
        }
    }

    private fun removeItem(item: PortfolioItem) {
        viewModelScope.launch {
            removePortfolioItemUseCase(item.stockInfo)
        }
    }

    private fun updateQuantity(item: PortfolioItem, quantity: Double) {
        viewModelScope.launch {
            addPortfolioItemUseCase(item.stockInfo, quantity)
            _uiState.update { it.copy(editingItem = null) }
        }
    }

    private fun loadInitialData() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            // Fetch exchange rate first
            getUsdEurUseCase().onSuccess { rate ->
                _usdToEurRate = rate
            }.onFailure {
                _usdToEurRate = 1.0 // Fallback or handle error
            }

            val jobs = mutableListOf<Job>()
            jobs.add(launch { getSymbols("US") })
            jobs.joinAll()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun updateSearchQuery(query: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = query,
                filteredSymbols = filterSymbols(currentState.symbols, query)
            )
        }
    }

    private fun selectSymbol(symbol: StockInfo) {
        viewModelScope.launch {
            addPortfolioItemUseCase(symbol, 0.0)
            // Navigate back to portfolio after adding
            _uiState.update { 
                it.copy(
                    currentScreen = PriceScreenType.Portfolio,
                    searchQuery = ""
                ) 
            }
        }
    }

    private fun moveSymbol(fromIndex: Int, toIndex: Int) {
        val currentList = _uiState.value.portfolioItems.toMutableList()
        if (fromIndex in currentList.indices && toIndex in currentList.indices) {
            val item = currentList.removeAt(fromIndex)
            currentList.add(toIndex, item)
            
            // Optimistic update
            _uiState.update { it.copy(portfolioItems = currentList) }
            
            // Switch to Custom Sort Mode
            if (!sharedPreferences.getBoolean(PREF_CUSTOM_SORT, false)) {
                sharedPreferences.edit().putBoolean(PREF_CUSTOM_SORT, true).apply()
            }

            viewModelScope.launch {
                // This saves the current list order (which includes the user's latest move)
                // effectively "locking in" the order as manual.
                reorderPortfolioItemsUseCase(currentList)
            }
        }
    }

    private fun filterSymbols(symbols: List<StockInfo>, query: String): List<StockInfo> {
        if (query.isBlank()) return symbols
        return symbols.filter {
            it.description.contains(query, ignoreCase = true) ||
            it.displaySymbol.contains(query, ignoreCase = true) ||
            it.figi.contains(query, ignoreCase = true) ||
            it.isin.contains(query, ignoreCase = true)
        }
    }

    private fun getSymbols(exchange: String) {
        viewModelScope.launch {
            val result = getSymbolsUseCase(exchange)
            result.onSuccess { symbols ->
                val sortedSymbols = symbols.sortedBy { it.displaySymbol }
                _uiState.update {
                    it.copy(
                        symbols = sortedSymbols,
                        filteredSymbols = sortedSymbols
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(symbols = emptyList(), filteredSymbols = emptyList()) }
            }
        }
    }
}
