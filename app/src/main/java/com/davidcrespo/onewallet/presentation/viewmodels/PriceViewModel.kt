package com.davidcrespo.onewallet.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidcrespo.onewallet.domain.model.PortfolioItem
import com.davidcrespo.onewallet.domain.model.finnhub.StockInfo
import com.davidcrespo.onewallet.domain.usecase.GetPriceUseCase
import com.davidcrespo.onewallet.domain.usecase.GetQuoteUseCase
import com.davidcrespo.onewallet.domain.usecase.GetSymbolsUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.AddPortfolioItemUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetPortfolioItemsUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.RemovePortfolioItemUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.ReorderPortfolioItemsUseCase
import com.davidcrespo.onewallet.presentation.contract.PriceIntent
import com.davidcrespo.onewallet.presentation.contract.PriceUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PriceViewModel(
    private val getPriceUseCase: GetPriceUseCase,
    private val getSymbolsUseCase: GetSymbolsUseCase,
    private val getQuoteUseCase: GetQuoteUseCase,
    private val getPortfolioItemsUseCase: GetPortfolioItemsUseCase,
    private val addPortfolioItemUseCase: AddPortfolioItemUseCase,
    private val reorderPortfolioItemsUseCase: ReorderPortfolioItemsUseCase,
    private val removePortfolioItemUseCase: RemovePortfolioItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PriceUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getPortfolioItemsUseCase().collect { items ->
                _uiState.update { it.copy(portfolioItems = items) }
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
            val jobs = mutableListOf<Job>()
            jobs.add(launch { getSymbols("US") })
            jobs.add(launch { getPrice("AAPL") })
            jobs.add(launch { getQuote("GOOGL") })
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
            addPortfolioItemUseCase(symbol, 0.0) // Default quantity 0.0
            updateSearchQuery("")
            // Clear search result/dropdown? The query change above does it if we bind it.
            // But we also want to close the dropdown if open.
            // The UI observes searchQuery. If we set it to "", filtered list becomes empty or we handle it.
            _uiState.update { it.copy(filteredSymbols = emptyList()) }
        }
    }

    private fun moveSymbol(fromIndex: Int, toIndex: Int) {
        val currentList = _uiState.value.portfolioItems.toMutableList()
        if (fromIndex in currentList.indices && toIndex in currentList.indices) {
            val item = currentList.removeAt(fromIndex)
            currentList.add(toIndex, item)
            
            // Optimistically update UI (though flow from DB will eventually update it)
            // Ideally we wait for DB, but for drag and drop responsiveness we might need this.
            // However, since we observe DB, if we update DB, it will emit new list.
            // If we update UI manually here, we might get a race condition with DB emission.
            // BUT, updating DB is async.
            // For now, let's just launch the reorder usecase.
            // The UI state update in `moveSymbol` was crucial for the drag animation consistency?
            // Actually, if we rely on DB flow, there might be a delay.
            // Let's keep optimistic update but be aware.
            _uiState.update { it.copy(portfolioItems = currentList) }
            
            viewModelScope.launch {
                reorderPortfolioItemsUseCase(currentList)
            }
        }
    }

    private fun filterSymbols(symbols: List<StockInfo>, query: String): List<StockInfo> {
        if (query.isBlank()) return emptyList()
        return symbols.filter {
            it.description.contains(query, ignoreCase = true) ||
            it.displaySymbol.contains(query, ignoreCase = true) ||
            it.figi.contains(query, ignoreCase = true) ||
            it.isin.contains(query, ignoreCase = true)
        }
    }

    private fun getPrice(symbol: String) {
        viewModelScope.launch {
            val result = getPriceUseCase(symbol)
            result.onSuccess { priceObj ->
                _uiState.update { it.copy(price = "$${priceObj.price}") }
            }.onFailure { error ->
                _uiState.update { it.copy(price = "Error: ${error.message}") }
            }
        }
    }

    private fun getSymbols(exchange: String) {
        viewModelScope.launch {
            val result = getSymbolsUseCase(exchange)
            result.onSuccess { symbols ->
                _uiState.update {
                    it.copy(
                        symbols = symbols,
                        filteredSymbols = filterSymbols(symbols, it.searchQuery)
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(symbols = emptyList(), filteredSymbols = emptyList()) }
            }
        }
    }

    private fun getQuote(symbol: String) {
        viewModelScope.launch {
            val result = getQuoteUseCase(symbol)
            result.onSuccess { quoteObj ->
                _uiState.update { it.copy(quote = "$${quoteObj.currentPrice}") }
            }.onFailure { error ->
                _uiState.update { it.copy(quote = "Error: ${error.message}") }
            }
        }
    }
}