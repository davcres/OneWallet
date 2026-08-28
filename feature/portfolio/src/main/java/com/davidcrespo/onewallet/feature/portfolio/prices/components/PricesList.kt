package com.davidcrespo.onewallet.feature.portfolio.prices.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.domain.model.investment.isMarket
import com.davidcrespo.onewallet.core.designsystem.composables.OWAnimatedList
import com.davidcrespo.onewallet.core.designsystem.composables.OWInvestmentItem
import com.davidcrespo.onewallet.core.designsystem.composables.auxiliar.SectionType
import com.davidcrespo.onewallet.core.models.CurrencyView
import com.davidcrespo.onewallet.core.models.InvestmentView
import com.davidcrespo.onewallet.feature.portfolio.models.PortfolioCoachmarks
import com.pseudoankit.coachmark.LocalCoachMarkScope
import com.pseudoankit.coachmark.model.ToolTipPlacement
import com.pseudoankit.coachmark.scope.enableCoachMark
import com.pseudoankit.coachmark.shape.Arrow
import com.pseudoankit.coachmark.shape.Balloon
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PricesList(
    items: ImmutableList<InvestmentView>,
    currency: CurrencyView,
    modifier: Modifier = Modifier,
    isBalanceVisible: Boolean = true,
    shouldAnimate: Boolean = true
) {
    if (shouldAnimate) {
        OWAnimatedList(
            items = items.filter { it.type.isMarket() }.toImmutableList(),
            key = { it.symbol },
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 32.dp),
            modifier = modifier
                .fillMaxSize()
                .enableCoachMark(
                    key = PortfolioCoachmarks.PRICES_LIST,
                    toolTipPlacement = ToolTipPlacement.Top,
                    tooltip = {
                        Balloon(
                            arrow = Arrow.Bottom(),
                            modifier = Modifier.widthIn(max = 200.dp),
                            bgColor = MaterialTheme.colorScheme.primaryContainer,
                            cornerRadius = 16.dp,
                            padding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = stringResource(PortfolioCoachmarks.PRICES_LIST.tooltip),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    coachMarkScope = LocalCoachMarkScope.current
                ),
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
