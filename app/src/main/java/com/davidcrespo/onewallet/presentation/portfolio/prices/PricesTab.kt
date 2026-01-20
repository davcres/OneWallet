package com.davidcrespo.onewallet.presentation.portfolio.prices

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import com.davidcrespo.onewallet.presentation.portfolio.prices.components.PricesList
import kotlinx.collections.immutable.ImmutableList

@Composable
fun PricesTab(
    currency: Currency,
    portfolioItems: ImmutableList<InvestmentView>,
    modifier: Modifier = Modifier
) {
    PricesList(
        items = portfolioItems,
        currency = currency,
        modifier = modifier
    )
}

