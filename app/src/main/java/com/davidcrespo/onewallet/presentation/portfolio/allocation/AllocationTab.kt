package com.davidcrespo.onewallet.presentation.portfolio.allocation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.core.composables.charts.models.AssetSlice
import com.davidcrespo.onewallet.domain.model.investment.InvestmentAttribute
import com.davidcrespo.onewallet.presentation.models.CurrencyView
import com.davidcrespo.onewallet.presentation.portfolio.PortfolioUiState
import com.davidcrespo.onewallet.presentation.portfolio.allocation.composables.AllocationList
import com.davidcrespo.onewallet.presentation.portfolio.allocation.composables.Graphic
import com.davidcrespo.onewallet.presentation.portfolio.allocation.mapper.toAllocationInvestmentView
import com.davidcrespo.onewallet.presentation.portfolio.allocation.models.ItemsByCategoryView
import com.davidcrespo.onewallet.presentation.portfolio.allocation.models.ItemsByTypeView
import com.davidcrespo.onewallet.presentation.portfolio.components.segmentedButton.SegmentedButton
import com.davidcrespo.onewallet.presentation.portfolio.components.segmentedButton.SegmentedItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
fun AllocationTab(
    itemsByType: ImmutableList<ItemsByTypeView>,
    itemsByCategory: ImmutableList<ItemsByCategoryView>,
    allocationMode: Int,
    totalBalance: Double,
    previousBalance: Double,
    currency: CurrencyView,
    onSelect: (InvestmentAttribute) -> Unit,
    onAllocationModeChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isBalanceVisible: Boolean = true,
    isActivePage: Boolean = true
) {
    val items = if (allocationMode == PortfolioUiState.ALLOCATION_BY_TYPE) {
        itemsByType.map { item ->
            val percentage = if (totalBalance != 0.0) item.totalValue / totalBalance * 100 else 0.0
            val name = stringResource(item.type.titleRes) + " (%.2f%%)".format(percentage)

            item.toAllocationInvestmentView(
                currency = currency,
                displayName = name
            )
        }.toImmutableList()
    } else {
        itemsByCategory.map { item ->
            val percentage = if (totalBalance != 0.0) item.totalValue / totalBalance * 100 else 0.0
            val categoryName = item.category.nameRes?.let { stringResource(it) } ?: item.category.id
            val name = categoryName + " (%.2f%%)".format(percentage)

            item.toAllocationInvestmentView(
                currency = currency,
                displayName = name
            )
        }.toImmutableList()
    }

    AllocationList(
        header = {
            Spacer(modifier = Modifier.height(16.dp))

            SegmentedButton(
                selectedIndex = allocationMode,
                items = persistentListOf(
                    SegmentedItem(PortfolioUiState.ALLOCATION_BY_TYPE, stringResource(R.string.tab_allocation_type), Icons.Default.PieChart),
                    SegmentedItem(PortfolioUiState.ALLOCATION_BY_CATEGORY, stringResource(R.string.tab_allocation_category), Icons.AutoMirrored.Filled.Label),
                ),
                onSelected = onAllocationModeChange,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Graphic(
                portfolioItems = if (allocationMode == PortfolioUiState.ALLOCATION_BY_TYPE) {
                    itemsByType.map {
                        AssetSlice(
                            name = stringResource(it.type.titleRes),
                            value = it.totalValue,
                            color = it.type.color
                        )
                    }.toImmutableList()
                } else {
                    itemsByCategory.map {
                        val categoryName = it.category.nameRes?.let { stringResource(it) } ?: it.category.id
                        AssetSlice(
                            name = categoryName,
                            value = it.totalValue,
                            color = it.category.color
                        )
                    }.toImmutableList()
                },
                totalBalance = totalBalance,
                previousBalance = previousBalance,
                currency = currency,
                isBalanceVisible = isBalanceVisible,
                isExpanded = true,
                shouldAnimate = isActivePage
            )
        },
        items = items,
        currency = currency,
        onSelect = { view ->
            val attribute = if (allocationMode == PortfolioUiState.ALLOCATION_BY_TYPE) view.type else view.category
            onSelect(attribute)
        },
        isBalanceVisible = isBalanceVisible,
        shouldAnimate = isActivePage,
        showTypeIcon = allocationMode == PortfolioUiState.ALLOCATION_BY_TYPE,
        modifier = modifier
    )
}
