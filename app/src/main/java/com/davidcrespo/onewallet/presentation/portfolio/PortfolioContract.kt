package com.davidcrespo.onewallet.presentation.portfolio

import com.davidcrespo.onewallet.domain.model.investment.Investment

data class PortfolioUiState(
    val portfolioItems: List<Investment> = listOf(),
    val symbolsWithPrice: List<String> = emptyList(),
    val usdEurRate: Double = 1.0,
    val totalBalance: Double = 0.0,
    val previousBalance: Double = 0.0,
    val editingItem: Investment? = null,
    val isFundDialogVisible: Boolean = false,
    val isBankDialogVisible: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed interface PortfolioIntent {
    data object UpdateBalance : PortfolioIntent

    data class EditQuantity(val item: Investment?) : PortfolioIntent
    data class UpdateQuantity(val item: Investment, val quantity: Double) : PortfolioIntent
    data class RemoveItem(val item: Investment) : PortfolioIntent

    data class AddFundItem(val name: String, val quantity: Double, val price: Double) : PortfolioIntent
    data object ShowFundDialog : PortfolioIntent
    data object DismissFundDialog : PortfolioIntent

    data class AddBankItem(val name: String, val quantity: Double) : PortfolioIntent
    data object ShowBankDialog : PortfolioIntent
    data object DismissBankDialog : PortfolioIntent

    data object NavigateToHistorical : PortfolioIntent
    data class NavigateToMarket(val isCrypto: Boolean) : PortfolioIntent

}
