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
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import com.davidcrespo.onewallet.presentation.portfolio.positions.components.PortfolioList
import com.davidcrespo.onewallet.presentation.portfolio.positions.components.TotalBalance
import kotlinx.collections.immutable.ImmutableList

@Composable
fun PositionsTab(
    currency: Currency,
    totalBalance: Double,
    previousBalance: Double,
    portfolioItems: ImmutableList<InvestmentView>,
    onRemoveItem: (InvestmentView) -> Unit,
    onEditQuantity: (InvestmentView) -> Unit,
    changeBalanceVisibility: () -> Unit,
    isBalanceVisible: Boolean,
    isActivePage: Boolean = true
) {
    PortfolioList(
        header = {
            Column {
                TotalBalance(
                    currency = currency,
                    totalBalance = totalBalance,
                    previousBalance = previousBalance,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
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
        shouldAnimate = isActivePage
    )
}
