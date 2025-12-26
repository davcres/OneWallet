package com.davidcrespo.onewallet.presentation.portfolio.prices.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.composables.OWAnimatedList
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.presentation.designsystem.composables.bounceClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PricesList(
    items: List<Investment>,
    modifier: Modifier = Modifier
) {
    OWAnimatedList(
        items = items.filter { it.type == InvestmentType.STOCK || it.type == InvestmentType.CRYPTO },
        key = { it.symbol },
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        itemContent = { modifier, priceItem ->
            PriceItemCard(
                item = priceItem,
                modifier = modifier
                    .bounceClick()
            )
        }
    )
}
