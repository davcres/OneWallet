package com.davidcrespo.onewallet.presentation.portfolio.allocation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.composables.charts.models.AssetSlice
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import com.davidcrespo.onewallet.presentation.portfolio.allocation.composables.AllocationList
import com.davidcrespo.onewallet.presentation.portfolio.allocation.composables.Graphic
import com.davidcrespo.onewallet.presentation.portfolio.allocation.models.ItemsByTypeView
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

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
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Graphic(
            portfolioItems = itemsByType.map {
                AssetSlice(
                    name = stringResource(it.type.titleRes),
                    value = it.totalValue,
                    color = it.type.color
                )
            }.toPersistentList(),
            totalBalance = totalBalance,
            previousBalance = previousBalance,
            currency = currency,
            isBalanceVisible = isBalanceVisible,
            shouldAnimate = isActivePage
        )

        //TODO*** pedirle a gemini que refactorice
        AllocationList(
            items = itemsByType.map { itemsByType ->
                val totalPreviousValue = itemsByType.items.sumOf { it.quantity * it.displayPreviousPrice }
                val changePercent = totalPreviousValue
                    .takeIf { it != 0.0 }
                    ?.let { ((itemsByType.totalValue - it) / it) * 100.0 } ?: 0.0
                InvestmentView(
                    symbol = stringResource(itemsByType.type.titleRes),
                    name = stringResource(itemsByType.type.titleRes),
                    quantity = 1.0,
                    displayPrice = itemsByType.totalValue,
                    displayPreviousPrice = totalPreviousValue,
                    originalPrice = itemsByType.totalValue,
                    originalPreviousPrice = totalPreviousValue,
                    originalCurrency = currency,
                    changePercent = changePercent,
                    type = itemsByType.type,
                    month = 0,
                    year = 0
                )
            }.toPersistentList(),
            currency = currency,
            onSelect = onSelect,
            isBalanceVisible = isBalanceVisible,
            shouldAnimate = isActivePage
        )
    }
}
