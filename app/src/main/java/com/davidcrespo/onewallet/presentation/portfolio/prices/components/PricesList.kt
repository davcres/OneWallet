package com.davidcrespo.onewallet.presentation.portfolio.prices.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.isMarket
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWAnimatedList
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWInvestmentItem
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.SectionType
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PricesList(
    items: ImmutableList<InvestmentView>,
    currency: Currency,
    modifier: Modifier = Modifier,
    isBalanceVisible: Boolean = true,
    shouldAnimate: Boolean = true
) {
    if (shouldAnimate) {
        OWAnimatedList(
            items = items.filter { it.type.isMarket() }.toPersistentList(),
            key = { it.symbol },
            contentPadding = PaddingValues(16.dp),
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            itemContent = { modifier, priceItem, index ->
                OWInvestmentItem(
                    item = priceItem,
                    currency = currency,
                    section = SectionType.PRICES,
                    onClick = {},
                    modifier = modifier,
                    isBalanceVisible = isBalanceVisible
                )
            }
        )
    } else {
        Box(modifier = modifier.fillMaxSize())
    }
}
