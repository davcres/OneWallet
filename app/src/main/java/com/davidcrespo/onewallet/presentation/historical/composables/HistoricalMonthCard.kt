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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.composables.animations.bounceClick
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.PriceDisplay
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.TrendDisplay
import com.davidcrespo.onewallet.presentation.designsystem.theme.CardGlowOuter
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import kotlinx.coroutines.delay
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HistoricalMonthCard(
    item: List<InvestmentView>,
    previousItem: List<InvestmentView>? = null,
    currency: Currency,
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

                    val balance = item.sumOf { it.quantity * it.displayPrice }
                    val previousBalance = previousItem?.sumOf { it.quantity * it.displayPrice } ?: 0.0
                    val marketValue = item.sumOf { it.displayPrice }
                    val previousMarketValue = previousItem?.sumOf { it.displayPrice } ?: 0.0

                    PriceDisplay(value = balance, currency = currency)
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
                                TrendDisplay(value = percentage, text = "%.2f %%".format(percentage), show, currency)
                            } else {
                                val variance = balance - previousBalance
                                TrendDisplay(value = variance, text = "%.2f €".format(variance), show, currency)
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
private fun GhostContent() {
    // Ghost Column to have same height in all items
    Column(
        modifier = Modifier.alpha(0f)
    ) {
        PriceDisplay(value = 0.0, currency = Currency.EUR)
        Spacer(modifier = Modifier.height(8.dp))
        TrendDisplay(value = 1.0, text = "0.00 %", false, Currency.EUR)
    }
}