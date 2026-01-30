package com.davidcrespo.onewallet.presentation.portfolio

import androidx.compose.runtime.Immutable
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

@Immutable
data class PortfolioUiState(
    val portfolioItems: ImmutableList<InvestmentView> = persistentListOf(),
    val portfolioItemsByType: ImmutableMap<InvestmentType, ImmutableList<InvestmentView>> = persistentMapOf(),
    val symbolsWithPrice: ImmutableList<String> = persistentListOf(),
    val selectedCurrency: Currency = Currency.EUR,
    val usdEurRate: Double = 1.0,
    val totalBalance: Double = 0.0,
    val previousBalance: Double = 0.0,
    val editingItem: InvestmentView? = null,
    val deletingItem: InvestmentView? = null,
    val isFundDialogVisible: Boolean = false,
    val isEtfDialogVisible: Boolean = false,
    val isBankDialogVisible: Boolean = false,
    val isOtherDialogVisible: Boolean = false,
    val typeDetail: InvestmentType? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed interface PortfolioIntent {
    data object UpdateBalance : PortfolioIntent
    data object ChangeCurrency : PortfolioIntent

    data class EditQuantity(val item: InvestmentView?) : PortfolioIntent
    data class UpdateQuantity(val item: InvestmentView, val quantity: Double) : PortfolioIntent
    data class RemoveItem(val item: InvestmentView) : PortfolioIntent
    data class ShowDeleteDialog(val item: InvestmentView?) : PortfolioIntent

    data class AddFundItem(val name: String, val quantity: Double) : PortfolioIntent
    data object ShowFundDialog : PortfolioIntent
    data object DismissFundDialog : PortfolioIntent

    data class AddEtfItem(val name: String, val quantity: Double) : PortfolioIntent
    data object ShowEtfDialog : PortfolioIntent
    data object DismissEtfDialog : PortfolioIntent

    data class AddBankItem(val name: String, val quantity: Double, val currency: Currency) : PortfolioIntent
    data object ShowBankDialog : PortfolioIntent
    data object DismissBankDialog : PortfolioIntent

    data class AddOtherItem(val name: String, val quantity: Double, val currency: Currency) : PortfolioIntent
    data object ShowOtherDialog : PortfolioIntent
    data object DismissOtherDialog : PortfolioIntent

    data class SetError(val error: String) : PortfolioIntent
    data object ClearError : PortfolioIntent

    data class NavigateToHistorical(val isBalanceVisible: Boolean) : PortfolioIntent
    data class NavigateToMarket(val isCrypto: Boolean) : PortfolioIntent

    data object GetItemsByType : PortfolioIntent
    data class SelectInvestmentType(val type: InvestmentType?) : PortfolioIntent
    data object DismissInvestmentType : PortfolioIntent
}
