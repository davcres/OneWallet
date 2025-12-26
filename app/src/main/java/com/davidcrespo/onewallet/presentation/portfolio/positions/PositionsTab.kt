package com.davidcrespo.onewallet.presentation.portfolio.positions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.presentation.portfolio.positions.components.PortfolioList
import com.davidcrespo.onewallet.presentation.portfolio.positions.components.TotalBalance

@Composable
fun PositionsTab(
    totalBalance: Double,
    previousBalance: Double,
    portfolioItems: List<Investment>,
    onRemoveItem: (Investment) -> Unit,
    onEditQuantity: (Investment) -> Unit
) {
    val listState = rememberLazyListState()
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
            totalBalance = totalBalance,
            previousBalance = previousBalance,
            modifier = Modifier.fillMaxWidth(),
            isExpanded = isExpanded.value
        )

        Text(
            text = "Tus inversiones",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        PortfolioList(
            items = portfolioItems,
            onRemove = onRemoveItem,
            onEdit = onEditQuantity,
            state = listState
        )
    }
}
