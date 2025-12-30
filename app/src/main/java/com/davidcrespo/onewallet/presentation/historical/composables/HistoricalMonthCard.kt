package com.davidcrespo.onewallet.presentation.historical.composables

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.core.composables.bounceClick
import com.davidcrespo.onewallet.presentation.designsystem.theme.CardGlowOuter
import kotlinx.coroutines.delay
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HistoricalMonthCard(
    item: List<Investment>,
    previousItem: List<Investment>? = null,
    onClick: () -> Unit,
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
            .fillMaxWidth()
            .bounceClick(),
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = CardGlowOuter),
        onClick = onClick
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
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            val monthName = Month.of(item.first().month)
                .getDisplayName(TextStyle.FULL, Locale.getDefault())
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

            Text(
                text = "$monthName ${item.first().year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Box(contentAlignment = Alignment.CenterEnd) {
                Column(horizontalAlignment = Alignment.End) {

                    val balance = item.sumOf { it.quantity * it.price }
                    val marketValue = item.sumOf { it.price }
                    val previousMarketValue = previousItem?.sumOf { it.price } ?: 0.0

                    PriceDisplay(value = balance)
                    if (marketValue != 0.0 && previousMarketValue != 0.0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        AnimatedContent(
                            targetState = showPercentageState,
                            transitionSpec = {
                                (slideInVertically { height -> height } + fadeIn())
                                    .togetherWith(slideOutVertically { height -> -height } + fadeOut())
                            },
                            label = "PercentageVarianceAnimation"
                        ) { show ->
                            if (show) {
                                val percentage = (marketValue - previousMarketValue) / previousMarketValue * 100
                                TrendDisplay(value = percentage, text = "%.2f %%".format(percentage))
                            } else {
                                val variance = marketValue - previousMarketValue
                                TrendDisplay(value = variance, text = "%.2f €".format(variance))
                            }
                        }
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
private fun TrendDisplay(value: Double, text: String) {
    Row {
        val (icon, color) = when {
            value > 0 -> Pair(
                Icons.AutoMirrored.Filled.TrendingUp,
                MaterialTheme.colorScheme.primary
            )

            value < 0 -> Pair(
                Icons.AutoMirrored.Filled.TrendingDown,
                MaterialTheme.colorScheme.error
            )

            else -> Pair(
                Icons.AutoMirrored.Filled.TrendingFlat,
                MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color,
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
        PriceDisplay(value = 0.0)
        Spacer(modifier = Modifier.height(8.dp))
        TrendDisplay(value = 1.0, text = "0.00 %")
    }
}