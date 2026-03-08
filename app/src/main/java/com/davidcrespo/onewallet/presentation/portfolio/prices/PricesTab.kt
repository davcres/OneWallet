package com.davidcrespo.onewallet.presentation.portfolio.prices

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.davidcrespo.onewallet.presentation.models.CurrencyView
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import com.davidcrespo.onewallet.presentation.portfolio.prices.components.PricesList
import kotlinx.collections.immutable.ImmutableList

@Composable
fun PricesTab(
    currency: CurrencyView,
    portfolioItems: ImmutableList<InvestmentView>,
    modifier: Modifier = Modifier,
    isBalanceVisible: Boolean = true,
    isActivePage: Boolean = true
) {
    PricesList(
        items = portfolioItems,
        currency = currency,
        modifier = modifier,
        isBalanceVisible = isBalanceVisible,
        shouldAnimate = isActivePage
    )
}

