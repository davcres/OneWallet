package com.davidcrespo.onewallet.presentation.portfolio.prices

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.presentation.portfolio.prices.components.PricesList

@Composable
fun PricesTab(
    portfolioItems: List<Investment>,
    modifier: Modifier = Modifier
) {
    PricesList(
        items = portfolioItems,
        modifier = modifier
    )
}

