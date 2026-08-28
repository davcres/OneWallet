package com.davidcrespo.onewallet.core.designsystem.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.composables.AutoScrollingText
import com.davidcrespo.onewallet.core.composables.modifiers.animations.bounceClick
import com.davidcrespo.onewallet.core.composables.modifiers.privacySensitive
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentCategory
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.core.models.CurrencyView
import com.davidcrespo.onewallet.core.models.InvestmentView
import com.davidcrespo.onewallet.core.models.color
import com.davidcrespo.onewallet.core.models.icon
import com.davidcrespo.onewallet.domain.model.investment.isMarket
import com.davidcrespo.onewallet.core.designsystem.composables.auxiliar.PercentageVarianceSwitcher
import com.davidcrespo.onewallet.core.designsystem.composables.auxiliar.PriceDisplay
import com.davidcrespo.onewallet.core.designsystem.composables.auxiliar.SectionType
import com.davidcrespo.onewallet.core.designsystem.composables.auxiliar.TrendDisplay
import com.davidcrespo.onewallet.core.designsystem.theme.OneWalletTheme
import com.davidcrespo.onewallet.core.models.contentDescription

@Composable
fun OWInvestmentItem(
    item: InvestmentView,
    currency: CurrencyView,
    previousMonthItem: InvestmentView? = null,
    section: SectionType,
    onClick: (InvestmentView) -> Unit,
    isBalanceVisible: Boolean,
    onGloballyPositioned: (LayoutCoordinates) -> Unit = {},
    modifier: Modifier = Modifier,
    isPressed: Boolean = false,
    showTypeIcon: Boolean = false
) {
    val totalValue = when (section) {
        SectionType.PORTFOLIO, SectionType.HISTORY -> item.quantity * item.displayPrice
        SectionType.PRICES, SectionType.ALLOCATION -> item.displayPrice
    }

    val itemContentDescription = item.contentDescription(totalValue, currency)

    Card(
        modifier = modifier
            .onGloballyPositioned { onGloballyPositioned(it) }
            .fillMaxWidth()
            .bounceClick(isPressed)
            .clearAndSetSemantics {
                contentDescription = itemContentDescription
            },
        shape = CircleShape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        onClick = { onClick(item) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))

            val boxColor = if (showTypeIcon) item.type.color else item.category.color
            val icon = if (showTypeIcon) item.type.icon else item.category.icon

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(boxColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                AutoScrollingText(
                    text = item.name.takeIf { it.isNotEmpty() } ?: item.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )

                if (item.name.isNotEmpty() && item.symbol != item.name) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = item.symbol,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier
                    .privacySensitive(hideContent = !isBalanceVisible)
                    .padding(all = 16.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    val showPercentage = item.type.isMarket()

                    if (showPercentage) {
                        Spacer(modifier = Modifier.height(4.dp))
                        PriceDisplay(value = totalValue, currency = currency)
                        Spacer(modifier = Modifier.height(8.dp))

                        val currentPrice = when (section) {
                            SectionType.PORTFOLIO -> item.displayPrice * item.quantity
                            else -> item.displayPrice
                        }
                        val previousPrice = when (section) {
                            SectionType.PORTFOLIO -> item.displayPreviousPrice * item.quantity
                            SectionType.HISTORY -> previousMonthItem?.displayPrice ?: 0.0
                            else -> item.displayPreviousPrice
                        }

                        PercentageVarianceSwitcher(
                            currentPrice = currentPrice,
                            previousPrice = previousPrice,
                            currency = currency
                        )
                    } else {
                        PriceDisplay(
                            value = totalValue,
                            currency = currency
                        )
                    }
                }

                GhostContent()
            }

            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
private fun GhostContent() {
    // Ghost Column to have same height in all items
    Column(
        modifier = Modifier
            .alpha(0f)
            .semantics { hideFromAccessibility() }
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        PriceDisplay(value = 0.0, currency = CurrencyView.get(EUR))
        Spacer(modifier = Modifier.height(8.dp))
        TrendDisplay(value = 1.0, text = "0.00 %", false, CurrencyView.get(EUR))
    }
}

@Preview
@Composable
private fun OWInvestmentItemPreview() {
    OneWalletTheme {
        OWInvestmentItem(
            item = InvestmentView(
                symbol = "AAPL",
                name = "Apple",
                quantity = 10.0,
                displayPrice = 150.0,
                displayPreviousPrice = 140.0,
                originalPrice = 150.0,
                originalPreviousPrice = 140.0,
                originalCurrency = CurrencyView.get(EUR),
                changePercent = 10.0,
                type = InvestmentType.STOCK,
                category = InvestmentCategory.Tech,
                month = 0,
                year = 0
            ),
            currency = CurrencyView.get(EUR),
            section = SectionType.PRICES,
            onClick = {},
            isBalanceVisible = true
        )
    }
}
