package com.davidcrespo.onewallet.presentation.designsystem.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CurrencyBitcoin
import androidx.compose.material.icons.outlined.PieChartOutline
import androidx.compose.material.icons.outlined.StackedLineChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.composables.bounceClick
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.PriceDisplay
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.SectionType
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.TrendDisplay
import com.davidcrespo.onewallet.presentation.designsystem.theme.CardGlowOuter
import kotlinx.coroutines.delay

@Composable
fun OWInvestmentItem(
    item: Investment,
    currency: Currency,
    previousMonthItem: Investment? = null,
    section: SectionType,
    onClick: (Investment) -> Unit,
    onGloballyPositioned: (LayoutCoordinates) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showPercentageState by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            showPercentageState = !showPercentageState
        }
    }

    Card(
        modifier = modifier
            .onGloballyPositioned { onGloballyPositioned(it) }
            .fillMaxWidth()
            .bounceClick(),
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = CardGlowOuter),
        onClick = { onClick(item) }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (item.type) {
                    InvestmentType.STOCK -> Icons.Outlined.StackedLineChart
                    InvestmentType.CRYPTO -> Icons.Outlined.CurrencyBitcoin
                    InvestmentType.FUND -> Icons.Outlined.PieChartOutline
                    InvestmentType.CASH -> Icons.Outlined.AccountBalance
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
                //TODO***
                /*Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )*/
            }

            Box(contentAlignment = Alignment.CenterEnd) {
                Column(horizontalAlignment = Alignment.End) {
                    val showPercentage = item.type == InvestmentType.STOCK || item.type == InvestmentType.CRYPTO
                    val totalValue = when (section) {
                        SectionType.PORTFOLIO, SectionType.HISTORICAL -> item.quantity * item.price
                        SectionType.PRICES -> item.price
                    }

                    if (showPercentage) {
                        Column(horizontalAlignment = Alignment.End) {
                            Spacer(modifier = Modifier.height(4.dp))
                            PriceDisplay(value = totalValue, currency = currency)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val currentPrice = when (section) {
                                SectionType.PORTFOLIO -> item.price * item.quantity
                                else -> item.price
                            }
                            val previousPrice = when (section) {
                                SectionType.PORTFOLIO -> item.previousPrice * item.quantity
                                SectionType.HISTORICAL -> previousMonthItem?.price ?: 0.0
                                else -> item.previousPrice
                            }

                            AnimatedContent(
                                targetState = showPercentageState,
                                transitionSpec = {
                                    (slideInVertically { height -> height } + fadeIn())
                                        .togetherWith(slideOutVertically { height -> -height } + fadeOut())
                                },
                                label = "PercentageVarianceAnimation"
                            ) { showPercentage ->
                                if (currentPrice == 0.0 || previousPrice == 0.0) return@AnimatedContent

                                if (showPercentage) {
                                    val percentage = (currentPrice - previousPrice) / previousPrice * 100
                                    TrendDisplay(value = percentage, text = "%.2f %%".format(percentage), showPercentage, currency)
                                } else {
                                    val variance = currentPrice - previousPrice
                                    TrendDisplay(value = variance, text = "$ %.2f".format(variance), showPercentage, currency)
                                }
                            }
                        }
                    } else {
                        PriceDisplay(value = totalValue, currency = currency)
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
        modifier = Modifier.alpha(0f)
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        PriceDisplay(value = 0.0, currency = Currency.EUR)
        Spacer(modifier = Modifier.height(8.dp))
        TrendDisplay(value = 1.0, text = "0.00 %", false, Currency.EUR)
    }
}
