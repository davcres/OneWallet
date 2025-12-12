package com.davidcrespo.onewallet.presentation.contract

import com.davidcrespo.onewallet.domain.model.PortfolioItem
import com.davidcrespo.onewallet.domain.model.finnhub.StockInfo

enum class PriceScreenType {
    Portfolio,
    AddInvestment,
    History
}

data class PriceUiState(
    val currentScreen: PriceScreenType = PriceScreenType.Portfolio,
    val price: String = "Wait...",
    val quote: String = "Wait...",
    val symbols: List<StockInfo> = emptyList(),
    val filteredSymbols: List<StockInfo> = emptyList(),
    val portfolioItems: List<PortfolioItem> = emptyList(),
    val totalBalance: Double = 0.0,
    val searchQuery: String = "",
    val editingItem: PortfolioItem? = null,
    val isBankDialogVisible: Boolean = false,
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
    data class RemoveItem(val item: PortfolioItem) : PriceIntent
    
    // Navigation Intents
    data object NavigateToAddInvestment : PriceIntent
    data object NavigateToHistory : PriceIntent
    data object NavigateBack : PriceIntent

    // Bank Dialog Intents
    data object ShowBankDialog : PriceIntent
    data object DismissBankDialog : PriceIntent
    data class AddBankItem(val name: String, val amount: Double) : PriceIntent
}
