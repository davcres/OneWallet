package com.davidcrespo.onewallet.presentation.portfolio.positions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
) {
    val isExpanded by remember {
        derivedStateOf { mutableStateOf(true) }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -10) {
                    isExpanded.value = false
                } else if (available.y > 10) {
                    isExpanded.value = true
                }
                return Offset.Zero
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TotalBalance(
            currency = currency,
            totalBalance = totalBalance,
            previousBalance = previousBalance,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            isExpanded = isExpanded.value,
            changeBalanceVisibility = changeBalanceVisibility,
            isBalanceVisible = isBalanceVisible
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = stringResource(R.string.your_investments),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(start = 16.dp)
        )

        PortfolioList(
            items = portfolioItems,
            currency = currency,
            onRemove = onRemoveItem,
            onEdit = onEditQuantity,
            isBalanceVisible = isBalanceVisible
        )
    }
}
