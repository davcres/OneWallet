package com.davidcrespo.onewallet.presentation.market.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import com.davidcrespo.onewallet.presentation.designsystem.composables.bounceClick

@Composable
fun MarketListItem(
    marketAsset: MarketAsset,
    isSelected: Boolean,
    addOneAsset: () -> Unit,
    selectAsset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .bounceClick()
            .clickable(onClick = addOneAsset)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = marketAsset.symbol,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
            if (!marketAsset.description.isNullOrEmpty()) {
                Text(
                    text = marketAsset.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = selectAsset,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = if (isSelected) {
                    Icons.Filled.AddCircle
                } else {
                    Icons.Default.AddCircleOutline
                },
                contentDescription = if (isSelected) "Unselect Asset" else "Select Asset",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview
@Composable
private fun MarketListItemPreview() {
    MarketListItem(
        marketAsset = MarketAsset(
            symbol = "AAPL",
            description = "Apple Inc.",
            type = InvestmentType.STOCK,
            currency = Currency.USD,
            figi = null,
            stockType = "STOCK"
        ),
        isSelected = false,
        addOneAsset = {},
        selectAsset = {}
    )
}
