package com.davidcrespo.onewallet.presentation.portfolio.positions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
import com.davidcrespo.onewallet.presentation.designsystem.theme.OneWalletTheme
import com.davidcrespo.onewallet.presentation.models.CurrencyView
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import com.davidcrespo.onewallet.presentation.portfolio.positions.components.PortfolioList
import com.davidcrespo.onewallet.presentation.portfolio.positions.components.TotalBalance
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun PositionsTab(
    currency: CurrencyView,
    totalBalance: Double,
    previousBalance: Double,
    portfolioItems: ImmutableList<InvestmentView>,
    onRemoveItem: (InvestmentView) -> Unit,
    onEditQuantity: (InvestmentView) -> Unit,
    changeBalanceVisibility: () -> Unit,
    isBalanceVisible: Boolean,
    isActivePage: Boolean = true,
    isEditOnboardingActive: Boolean = false,
    isDeleteOnboardingActive: Boolean = false
) {
    PortfolioList(
        header = {
            Column {
                TotalBalance(
                    currency = currency,
                    totalBalance = totalBalance,
                    previousBalance = previousBalance,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    isExpanded = true,
                    changeBalanceVisibility = changeBalanceVisibility,
                    isBalanceVisible = isBalanceVisible,
                    shouldAnimate = isActivePage
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.your_investments),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        },
        items = portfolioItems,
        currency = currency,
        onRemove = onRemoveItem,
        onEdit = onEditQuantity,
        isBalanceVisible = isBalanceVisible,
        shouldAnimate = isActivePage,
        isEditOnboardingActive = isEditOnboardingActive,
        isDeleteOnboardingActive = isDeleteOnboardingActive
    )
}

@Preview
@Composable
private fun PositionsTabPreview() {
    OneWalletTheme {
        PositionsTab(
            currency = CurrencyView.get(USD),
            totalBalance = 10.0,
            previousBalance = 9.0,
            portfolioItems = persistentListOf(
                InvestmentView(
                    symbol = "AAPL",
                    name = "Apple",
                    quantity = 10.0,
                    type = InvestmentType.STOCK,
                    originalCurrency = CurrencyView.get(USD),
                    originalPrice = 150.0,
                    originalPreviousPrice = 140.0,
                    displayPrice = 150.0,
                    displayPreviousPrice = 140.0,
                    changePercent = 0.0,
                    month = 0,
                    year = 0
                )
            ),
            onRemoveItem = {},
            onEditQuantity = {},
            changeBalanceVisibility = {},
            isBalanceVisible = true
        )
    }
}
