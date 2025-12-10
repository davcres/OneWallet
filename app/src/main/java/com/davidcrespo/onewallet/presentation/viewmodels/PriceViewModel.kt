package com.davidcrespo.onewallet.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidcrespo.onewallet.domain.model.finnhub.StockInfo
import com.davidcrespo.onewallet.domain.usecase.GetPriceUseCase
import com.davidcrespo.onewallet.domain.usecase.GetQuoteUseCase
import com.davidcrespo.onewallet.domain.usecase.GetSymbolsUseCase
import com.davidcrespo.onewallet.presentation.contract.PriceIntent
import com.davidcrespo.onewallet.presentation.contract.PriceUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PriceViewModel(
    private val getPriceUseCase: GetPriceUseCase,
    private val getSymbolsUseCase: GetSymbolsUseCase,
    private val getQuoteUseCase: GetQuoteUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PriceUiState())
    val uiState = _uiState.asStateFlow()

    fun handleIntent(intent: PriceIntent) {
        when (intent) {
            is PriceIntent.LoadInitialData -> loadInitialData()
            is PriceIntent.SearchQueryChanged -> updateSearchQuery(intent.query)
            is PriceIntent.SelectSymbol -> selectSymbol(intent.symbol)
        }
    }

    private fun loadInitialData() {
        getSymbols("US")
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
        updateSearchQuery(symbol.displaySymbol)
        // Optionally fetch price/quote for selected symbol here if desired
    }

    private fun filterSymbols(symbols: List<StockInfo>, query: String): List<StockInfo> {
        if (query.isBlank()) return emptyList()
        return symbols.filter {
            it.description.contains(query, ignoreCase = true) ||
            it.displaySymbol.contains(query, ignoreCase = true) ||
            it.figi.contains(query, ignoreCase = true)
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
