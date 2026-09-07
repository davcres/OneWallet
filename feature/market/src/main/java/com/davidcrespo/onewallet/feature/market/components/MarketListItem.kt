package com.davidcrespo.onewallet.feature.market.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.R
import com.davidcrespo.onewallet.core.composables.modifiers.animations.bounceClick
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.GlobalMarketRegion
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.MarketType
import com.davidcrespo.onewallet.domain.model.investment.USD
import com.davidcrespo.onewallet.core.designsystem.theme.OneWalletTheme
import com.davidcrespo.onewallet.core.models.*
import com.davidcrespo.onewallet.core.models.MarketAssetView

@Composable
fun MarketListItem(
    marketType: MarketType,
    marketAsset: MarketAssetView,
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
        val region = marketAsset.region
        if (marketType == MarketType.GLOBAL && region != null) {
            Image(
                painter = painterResource(region.flagRes),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))
        }

        Column {
            Text(
                text = marketAsset.symbol,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
            if (marketAsset.type == InvestmentType.STOCK) {
                val description = marketAsset.description
                if (!description.isNullOrEmpty()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (marketAsset.type == InvestmentType.CRYPTO) {
                Text(
                    text = if (marketAsset.currency.code == EUR) "${marketAsset.price} €" else "$${marketAsset.price}",
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
                contentDescription = if (isSelected) stringResource(R.string.unselect_asset_cd) else stringResource(R.string.select_asset_cd),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview
@Composable
private fun MarketListItemPreview() {
    OneWalletTheme {
        MarketListItem(
            marketType = MarketType.GLOBAL,
            marketAsset = MarketAssetView(
                symbol = "AAPL",
                price = 0.0,
                description = "Apple Inc.",
                type = InvestmentType.CRYPTO,
                currency = CurrencyView.get(USD),
                figi = null,
                region = GlobalMarketRegion.SPAIN,
                stockType = "STOCK"
            ),
            isSelected = false,
            addOneAsset = {},
            selectAsset = {}
        )
    }
}
