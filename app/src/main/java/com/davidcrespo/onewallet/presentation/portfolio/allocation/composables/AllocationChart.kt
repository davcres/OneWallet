package com.davidcrespo.onewallet.presentation.portfolio.allocation.composables

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.composables.charts.composables.ChartLegend
import com.davidcrespo.onewallet.core.composables.charts.composables.ChartSequentialAnimation
import com.davidcrespo.onewallet.core.composables.charts.models.AssetSlice
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWBalance
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.SectionType
import com.davidcrespo.onewallet.presentation.models.CurrencyView
import kotlinx.collections.immutable.ImmutableList

val BigChartSize = 250.dp
val SmallChartSize = 125.dp
val BigChartStrokeWidth = 28.dp
val SmallChartStrokeWidth = 16.dp

@Composable
fun Graphic(
    portfolioItems: ImmutableList<AssetSlice>,
    totalBalance: Double,
    previousBalance: Double,
    currency: CurrencyView,
    isBalanceVisible: Boolean,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    shouldAnimate: Boolean = true
) {
    val chartSize by animateDpAsState(targetValue = if (isExpanded) BigChartSize else SmallChartSize, label = "chartSize")
    val strokeWidth by animateDpAsState(targetValue = if (isExpanded) BigChartStrokeWidth else SmallChartStrokeWidth, label = "strokeWidth")

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(chartSize)
        ) {
            ChartSequentialAnimation(
                slices = portfolioItems,
                strokeWidth = strokeWidth,
                modifier = Modifier.fillMaxSize(),
                shouldAnimate = shouldAnimate
            )

            OWBalance(
                currency = currency,
                balance = totalBalance,
                previousBalance = previousBalance,
                isBalanceVisible = isBalanceVisible,
                isExpanded = isExpanded,
                section = SectionType.ALLOCATION,
                modifier = Modifier
                    .size(BigChartSize - BigChartStrokeWidth),
                shouldAnimate = shouldAnimate
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ChartLegend(
            portfolioItems = portfolioItems,
            style = if (isExpanded)
                MaterialTheme.typography.titleMedium
            else
                MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}
