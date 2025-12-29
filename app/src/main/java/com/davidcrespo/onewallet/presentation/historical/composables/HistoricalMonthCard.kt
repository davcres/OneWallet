package com.davidcrespo.onewallet.presentation.historical.composables

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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.presentation.designsystem.composables.bounceClick
import com.davidcrespo.onewallet.presentation.designsystem.theme.CardGlowOuter
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
                    val previousBalance = previousItem?.sumOf { it.quantity * it.price } ?: 0.0

                    Column(horizontalAlignment = Alignment.End) {
                        PriceDisplay(value = balance)
                        if (balance != 0.0 && previousBalance != 0.0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            PercentageDisplay(current = balance, previous = previousBalance)
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
private fun PercentageDisplay(current: Double, previous: Double) {
    Row {
        if (current == 0.0 || previous == 0.0) return@Row

        val percentage = (current - previous) / previous * 100

        val (percentageIcon, percentageColor) = when {
            percentage > 0 -> Pair(
                Icons.AutoMirrored.Filled.TrendingUp,
                MaterialTheme.colorScheme.primary
            )

            else -> Pair(
                Icons.AutoMirrored.Filled.TrendingDown,
                MaterialTheme.colorScheme.error
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
fun GhostContent() {
    // Ghost Column to have same height in all items
    Column(
        modifier = Modifier.alpha(0f)
    ) {
        PriceDisplay(value = 0.0)
        Spacer(modifier = Modifier.height(8.dp))
        PercentageDisplay(current = 1.0, previous = 1.0)
    }
}