package com.davidcrespo.onewallet.presentation.portfolio.allocation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
        modifier = modifier
            .nestedScroll(nestedScrollConnection),
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
            }.toImmutableList(),
            totalBalance = totalBalance,
            previousBalance = previousBalance,
            currency = currency,
            isBalanceVisible = isBalanceVisible,
            isExpanded = isExpanded.value,
            shouldAnimate = isActivePage
        )

        AllocationList(
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
            shouldAnimate = isActivePage
        )
    }
}
