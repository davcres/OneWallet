package com.davidcrespo.onewallet.feature.portfolio

import androidx.compose.runtime.Immutable
import com.davidcrespo.onewallet.domain.model.ThemeMode
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentAttribute
import com.davidcrespo.onewallet.domain.model.investment.InvestmentCategory
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.core.models.CurrencyView
import com.davidcrespo.onewallet.core.models.InvestmentView
import com.davidcrespo.onewallet.feature.portfolio.allocation.models.ItemsByCategoryView
import com.davidcrespo.onewallet.feature.portfolio.allocation.models.ItemsByTypeView
import com.davidcrespo.onewallet.feature.portfolio.models.PortfolioCoachmarks
import com.davidcrespo.onewallet.feature.portfolio.models.PortfolioTabs
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class PortfolioUiState(
    val portfolioItems: ImmutableList<InvestmentView> = persistentListOf(),
    val portfolioItemsByType: ImmutableList<ItemsByTypeView> = persistentListOf(),
    val portfolioItemsByCategory: ImmutableList<ItemsByCategoryView> = persistentListOf(),
    val allocationMode: Int = ALLOCATION_BY_TYPE,
    val symbolsWithPrice: ImmutableList<String> = persistentListOf(),
    val selectedCurrency: CurrencyView = CurrencyView.get(EUR),
    val totalBalance: Double = 0.0,
    val previousBalance: Double = 0.0,
    val editingItem: InvestmentView? = null,
    val deletingItem: InvestmentView? = null,
    val isAddInvestmentVisible: Boolean = false,
    val isFundDialogVisible: Boolean = false,
    val isEtfDialogVisible: Boolean = false,
    val isBankDialogVisible: Boolean = false,
    val isOtherDialogVisible: Boolean = false,
    val attributeDetail: InvestmentAttribute? = null,
    val isLoading: Boolean = true,
    val isLoadingBottomSheet: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val error: String? = null,
    val onboardingPlaylist: ImmutableList<PortfolioCoachmarks> = persistentListOf(),
    val isOnboardingCompleted: Boolean = false,
    val showOnboardingCompletionDialog: Boolean = false,
    val selectedTab: PortfolioTabs = PortfolioTabs.POSITIONS
) {
    companion object {
        const val ALLOCATION_BY_TYPE = 0
        const val ALLOCATION_BY_CATEGORY = 1
    }
}

sealed interface PortfolioEffect {
    data class NavigateToMarket(val isCrypto: Boolean) : PortfolioEffect
}

sealed interface PortfolioIntent {
    data object ChangeCurrency : PortfolioIntent
    data class SetTab(val tab: PortfolioTabs) : PortfolioIntent
    data class ToggleTheme(val themeMode: ThemeMode) : PortfolioIntent

    data class EditQuantity(val item: InvestmentView?) : PortfolioIntent
    data class UpdateQuantity(val item: InvestmentView, val quantity: Double, val alertThreshold: Double?, val category: InvestmentCategory) : PortfolioIntent
    data class RemoveItem(val item: InvestmentView) : PortfolioIntent
    data class ShowDeleteDialog(val item: InvestmentView?) : PortfolioIntent

    data object ShowAddInvestment : PortfolioIntent
    data object DismissAddInvestment : PortfolioIntent

    data class AddFundItem(val name: String, val quantity: Double) : PortfolioIntent
    data object ShowFundDialog : PortfolioIntent
    data object DismissFundDialog : PortfolioIntent

    data class AddEtfItem(val name: String, val quantity: Double) : PortfolioIntent
    data object ShowEtfDialog : PortfolioIntent
    data object DismissEtfDialog : PortfolioIntent

    data class AddBankItem(val name: String, val quantity: Double, val currency: CurrencyView) : PortfolioIntent
    data object ShowBankDialog : PortfolioIntent
    data object DismissBankDialog : PortfolioIntent

    data class AddOtherItem(val name: String, val quantity: Double, val currency: CurrencyView) : PortfolioIntent
    data object ShowOtherDialog : PortfolioIntent
    data object DismissOtherDialog : PortfolioIntent

    data class SetError(val error: String) : PortfolioIntent
    data object ClearError : PortfolioIntent

    data class NavigateToMarket(val isCrypto: Boolean) : PortfolioIntent

    data class SelectAttribute(val attribute: InvestmentAttribute?) : PortfolioIntent
    data object DismissAttributeDetail : PortfolioIntent

    data object StartOnboarding : PortfolioIntent
    data object NextOnboardingStep : PortfolioIntent
    data object ShowOnboardingCompletionDialog : PortfolioIntent
    data object DismissOnboardingCompletionDialog : PortfolioIntent
    data object ClearPortfolio : PortfolioIntent
    data class ChangeAllocationMode(val modeIndex: Int) : PortfolioIntent
}
