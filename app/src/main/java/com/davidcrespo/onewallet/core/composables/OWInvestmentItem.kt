package com.davidcrespo.onewallet.core.composables

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CurrencyBitcoin
import androidx.compose.material.icons.outlined.PieChartOutline
import androidx.compose.material.icons.outlined.StackedLineChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.composables.auxiliar.SectionType
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.presentation.designsystem.composables.bounceClick
import com.davidcrespo.onewallet.presentation.designsystem.theme.CardGlowOuter

@Composable
fun OWInvestmentItem(
    item: Investment,
    previousMonthItem: Investment? = null,
    section: SectionType,
    onClick: (Investment) -> Unit,
    onGloballyPositioned: (LayoutCoordinates) -> Unit = {},
    modifier: Modifier = Modifier
) {
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
                            PriceDisplay(value = totalValue)
                            Spacer(modifier = Modifier.height(8.dp))
                            PercentageDisplay(
                                current = item.price,
                                previous = if (section == SectionType.HISTORICAL)
                                            previousMonthItem?.price ?: 0.0
                                            else item.previousPrice
                            )
                        }
                    } else {
                        PriceDisplay(value = totalValue)
                    }
                }

                GhostContent()
            }

            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
private fun PriceDisplay(value: Double) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = "%.2f €".format(value),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun PercentageDisplay(current: Double, previous: Double) {
    Row {
        if (current == 0.0 || previous == 0.0) return@Row

        val percentage = (current - previous) / previous * 100

        val (percentageIcon, percentageColor) = when {
            percentage > 0 -> Pair(
                Icons.AutoMirrored.Filled.TrendingUp,
                MaterialTheme.colorScheme.primary
            )

            percentage < 0 -> Pair(
                Icons.AutoMirrored.Filled.TrendingDown,
                MaterialTheme.colorScheme.error
            )

            else -> Pair(
                Icons.AutoMirrored.Filled.TrendingFlat,
                MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = percentageIcon,
            contentDescription = "Percentage Icon",
            tint = percentageColor
        )
        Text(
            text = "%.2f %%".format(percentage),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = percentageColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun GhostContent() {
    // Ghost Column to have same height in all items
    Column(
        modifier = Modifier.alpha(0f)
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        PriceDisplay(value = 0.0)
        Spacer(modifier = Modifier.height(8.dp))
        PercentageDisplay(current = 1.0, previous = 1.0)
    }
}