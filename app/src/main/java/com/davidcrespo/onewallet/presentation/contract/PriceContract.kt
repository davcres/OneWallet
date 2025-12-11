package com.davidcrespo.onewallet.presentation.contract

import com.davidcrespo.onewallet.domain.model.PortfolioItem
import com.davidcrespo.onewallet.domain.model.finnhub.StockInfo

data class PriceUiState(
    val price: String = "Wait...",
    val quote: String = "Wait...",
    val symbols: List<StockInfo> = emptyList(),
    val filteredSymbols: List<StockInfo> = emptyList(),
    val portfolioItems: List<PortfolioItem> = emptyList(),
    val searchQuery: String = "",
    val editingItem: PortfolioItem? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed interface PriceIntent {
    data object LoadInitialData : PriceIntent
    data class SearchQueryChanged(val query: String) : PriceIntent
    data class SelectSymbol(val symbol: StockInfo) : PriceIntent
    data class MoveSymbol(val fromIndex: Int, val toIndex: Int) : PriceIntent
    data class EditQuantity(val item: PortfolioItem?) : PriceIntent
    data class UpdateQuantity(val item: PortfolioItem, val quantity: Double) : PriceIntent
    data class UpdateDca(
        val item: PortfolioItem, 
        val amount: Double, 
        val frequency: String,
        val startDate: Long?,
        val initialInvestment: Double
    ) : PriceIntent
    data class RemoveItem(val item: PortfolioItem) : PriceIntent
}
