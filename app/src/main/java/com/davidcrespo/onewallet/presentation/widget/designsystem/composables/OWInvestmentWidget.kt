package com.davidcrespo.onewallet.presentation.widget.designsystem.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.domain.model.investment.isMarket
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.SectionType
import com.davidcrespo.onewallet.presentation.models.CurrencyView
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import com.davidcrespo.onewallet.presentation.widget.utils.formatPrice
import com.davidcrespo.onewallet.presentation.widget.utils.formatTrendPercent
import com.davidcrespo.onewallet.presentation.widget.utils.trendColor

@Composable
fun OWInvestmentWidget(
    item: InvestmentView,
    currency: CurrencyView,
    previousMonthItem: InvestmentView? = null,
    section: SectionType,
    showPercentageInsteadOfVariance: Boolean = true,
    modifier: GlanceModifier = GlanceModifier,
) {
    val showTrend = item.type.isMarket()
    val totalValue = when (section) {
        SectionType.PORTFOLIO, SectionType.HISTORICAL -> item.quantity * item.displayPrice
        SectionType.PRICES, SectionType.ALLOCATION -> item.displayPrice
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1b2620))
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .cornerRadius(999.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono circular (simulado: box con background + padding)
            Box(
                modifier = GlanceModifier
                    .size(40.dp)
                    .background(ImageProvider(R.drawable.widget_round_background))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    provider = ImageProvider(item.getIconRes()),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.primary)
                )
            }

            Spacer(modifier = GlanceModifier.width(16.dp))

            // Nombre + símbolo
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = item.name.takeIf { it.isNotBlank() } ?: item.symbol,
                    maxLines = 1,
                    style = TextStyle(
                        color = GlanceTheme.colors.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                )

                if (item.name.isNotBlank() && item.symbol != item.name) {
                    Spacer(modifier = GlanceModifier.height(6.dp))
                    Text(
                        text = item.symbol,
                        maxLines = 1,
                        style = TextStyle(color = GlanceTheme.colors.onPrimaryContainer)
                    )
                }
            }

            // Precio + tendencia (derecha)
            Column(
                modifier = GlanceModifier.padding(start = 12.dp),
                horizontalAlignment = Alignment.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Price
                Text(
                    text = formatPrice(totalValue, currency, false),
                    maxLines = 1,
                    style = TextStyle(color = GlanceTheme.colors.primary, fontWeight = FontWeight.Bold)
                )

                if (showTrend) {
                    Spacer(modifier = GlanceModifier.height(6.dp))

                    val currentPrice = when (section) {
                        SectionType.PORTFOLIO -> item.displayPrice * item.quantity
                        else -> item.displayPrice
                    }
                    val previousPrice = when (section) {
                        SectionType.PORTFOLIO -> item.displayPreviousPrice * item.quantity
                        SectionType.HISTORICAL -> previousMonthItem?.displayPrice ?: 0.0
                        else -> item.displayPreviousPrice
                    }

                    if (currentPrice != 0.0 && previousPrice != 0.0) {
                        if (showPercentageInsteadOfVariance) {
                            val pct = (currentPrice - previousPrice) / previousPrice * 100.0
                            Text(
                                text = formatTrendPercent(pct, true),
                                maxLines = 1,
                                style = TextStyle(color = trendColor(pct))
                            )
                        } else {
                            val variance = currentPrice - previousPrice
                            Text(
                                text = formatPrice(variance, currency, true),
                                maxLines = 1,
                                style = TextStyle(color = trendColor(variance))
                            )
                        }
                    }
                }
            }
        }
    }
}
