package com.davidcrespo.onewallet.presentation.portfolio.positions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TotalBalance(
            totalBalance = totalBalance,
            previousBalance = previousBalance,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tus inversiones",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        PortfolioList(
            items = portfolioItems,
            onRemove = onRemoveItem,
            onEdit = onEditQuantity,
        )
    }
}
