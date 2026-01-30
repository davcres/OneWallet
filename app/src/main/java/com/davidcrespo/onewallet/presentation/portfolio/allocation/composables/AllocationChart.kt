package com.davidcrespo.onewallet.presentation.portfolio.allocation.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.core.composables.charts.composables.ChartLegend
import com.davidcrespo.onewallet.core.composables.charts.composables.ChartSequentialAnimation
import com.davidcrespo.onewallet.core.composables.charts.models.AssetSlice
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWCurrencyPrice
import kotlinx.collections.immutable.ImmutableList

@Composable
fun Graphic(
    portfolioItems: ImmutableList<AssetSlice>,
    totalBalance: Double,
    previousBalance: Double,
    currency: Currency,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(220.dp)
        ) {
            ChartSequentialAnimation(
                slices = portfolioItems,
                modifier = Modifier.fillMaxSize()
            )

            val variance = totalBalance - previousBalance
            val percentage =
                if (totalBalance == 0.0 || previousBalance == 0.0) {
                    0.0
                } else {
                    variance / previousBalance * 100
                }

            val (color, prefix) = when {
                percentage > 0 -> Pair(
                    MaterialTheme.colorScheme.primary,
                    "+"
                )
                percentage < 0 -> Pair(
                    MaterialTheme.colorScheme.error,
                    ""
                )
                else -> Pair(
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    ""
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.total_balance),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OWCurrencyPrice(
                    price = totalBalance,
                    currency = currency,
                    fontSize = 24.sp
                )
                Text(
                    text = "$prefix%.2f %%".format(percentage),
                    color = color,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ChartLegend(
            portfolioItems = portfolioItems,
            modifier = Modifier.padding(16.dp)
        )
    }
}
