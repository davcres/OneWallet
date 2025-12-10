package com.davidcrespo.onewallet.presentation.contract

import com.davidcrespo.onewallet.domain.model.finnhub.StockInfo

data class PriceUiState(
    val price: String = "Wait...",
    val quote: String = "Wait...",
    val symbols: List<StockInfo> = emptyList(),
    val filteredSymbols: List<StockInfo> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface PriceIntent {
    data object LoadInitialData : PriceIntent
    data class SearchQueryChanged(val query: String) : PriceIntent
    data class SelectSymbol(val symbol: StockInfo) : PriceIntent
}
