package com.davidcrespo.onewallet.presentation.portfolio

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.presentation.models.InvestmentView

data class PortfolioUiState(
    val portfolioItems: List<InvestmentView> = listOf(),
    val symbolsWithPrice: List<String> = emptyList(),
    val selectedCurrency: Currency = Currency.EUR,
    val usdEurRate: Double = 1.0,
    val totalBalance: Double = 0.0,
    val previousBalance: Double = 0.0,
    val editingItem: InvestmentView? = null,
    val isFundDialogVisible: Boolean = false,
    val isBankDialogVisible: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed interface PortfolioIntent {
    data object UpdateBalance : PortfolioIntent
    data object ChangeCurrency : PortfolioIntent

    data class EditQuantity(val item: InvestmentView?) : PortfolioIntent
    data class UpdateQuantity(val item: InvestmentView, val quantity: Double) : PortfolioIntent
    data class RemoveItem(val item: InvestmentView) : PortfolioIntent

    data class AddFundItem(val name: String, val quantity: Double, val price: Double) : PortfolioIntent
    data object ShowFundDialog : PortfolioIntent
    data object DismissFundDialog : PortfolioIntent

    data class AddBankItem(val name: String, val quantity: Double) : PortfolioIntent
    data object ShowBankDialog : PortfolioIntent
    data object DismissBankDialog : PortfolioIntent

    data object NavigateToHistorical : PortfolioIntent
    data class NavigateToMarket(val isCrypto: Boolean) : PortfolioIntent

}
