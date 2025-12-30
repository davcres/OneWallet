package com.davidcrespo.onewallet.presentation.historical.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CurrencyBitcoin
import androidx.compose.material.icons.outlined.PieChartOutline
import androidx.compose.material.icons.outlined.StackedLineChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.core.composables.DashedDivider

@Composable
fun HistoricalInvestmentDetail(
    investment: Investment,
    previousMonthInvestment: Investment?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percentage =
        if (previousMonthInvestment?.price == null || investment.price == 0.0 || previousMonthInvestment.price == 0.0) {
            0.0
        } else {
            (investment.price - previousMonthInvestment.price) / previousMonthInvestment.price * 100
        }
    val (percentageIcon, percentageColor, prefix) = when {
        percentage > 0 -> Triple(
            Icons.AutoMirrored.Filled.TrendingUp,
            MaterialTheme.colorScheme.primary,
            "+"
        )

        percentage < 0 -> Triple(
            Icons.AutoMirrored.Filled.TrendingDown,
            MaterialTheme.colorScheme.error,
            ""
        )

        else -> Triple(
            Icons.AutoMirrored.Filled.TrendingFlat,
            MaterialTheme.colorScheme.onSurfaceVariant,
            ""
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onTertiary)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = when (investment.type) {
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

                    Text(
                        text = investment.symbol,
                        style = MaterialTheme.typography.titleLarge
                    )

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Unidades en cartera",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Precio por unidad",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "%.2f".format(investment.quantity),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "%.2f €".format(investment.price),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Valor total",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "%.2f €".format(investment.quantity * investment.price),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if ((investment.type == InvestmentType.STOCK || investment.type == InvestmentType.CRYPTO) && (previousMonthInvestment != null && previousMonthInvestment.price != 0.0)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = percentageIcon,
                                            contentDescription = "Percentage Icon",
                                            tint = percentageColor
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = "($prefix%.2f %%)".format(percentage),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = percentageColor
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if ((investment.type == InvestmentType.STOCK || investment.type == InvestmentType.CRYPTO) && (previousMonthInvestment != null && previousMonthInvestment.price != 0.0)) {
                        DashedDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "P/L (Mes)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "$prefix%.2f €".format(investment.price - previousMonthInvestment.price),
                                style = MaterialTheme.typography.titleLarge,
                                color = percentageColor,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}