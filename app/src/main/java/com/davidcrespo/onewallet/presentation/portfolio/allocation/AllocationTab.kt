package com.davidcrespo.onewallet.presentation.portfolio.allocation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.composables.charts.models.AssetSlice
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.presentation.portfolio.allocation.composables.AllocationList
import com.davidcrespo.onewallet.presentation.portfolio.allocation.composables.Graphic
import com.davidcrespo.onewallet.presentation.portfolio.allocation.mapper.toAllocationInvestmentView
import com.davidcrespo.onewallet.presentation.portfolio.allocation.models.ItemsByTypeView
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun AllocationTab(
    itemsByType: ImmutableList<ItemsByTypeView>,
    totalBalance: Double,
    previousBalance: Double,
    currency: Currency,
    onSelect: (InvestmentType) -> Unit,
    modifier: Modifier = Modifier,
    isBalanceVisible: Boolean = true,
    isActivePage: Boolean = true
) {
    AllocationList(
        header = {
            Spacer(modifier = Modifier.height(16.dp))

            Graphic(
                portfolioItems = itemsByType.map {
                    AssetSlice(
                        name = stringResource(it.type.titleRes),
                        value = it.totalValue,
                        color = it.type.color
                    )
                }.toImmutableList(),
                totalBalance = totalBalance,
                previousBalance = previousBalance,
                currency = currency,
                isBalanceVisible = isBalanceVisible,
                isExpanded = true,
                shouldAnimate = isActivePage
            )
        },
        items = itemsByType.map { item ->
            val percentage = if (totalBalance != 0.0) item.totalValue / totalBalance * 100 else 0.0
            val name = stringResource(item.type.titleRes) + " (%.2f%%)".format(percentage)

            item.toAllocationInvestmentView(
                currency = currency,
                displayName = name
            )
        }.toImmutableList(),
        currency = currency,
        onSelect = onSelect,
        isBalanceVisible = isBalanceVisible,
        shouldAnimate = isActivePage,
        modifier = modifier
    )
}
