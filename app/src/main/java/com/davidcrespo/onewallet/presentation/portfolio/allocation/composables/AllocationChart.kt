package com.davidcrespo.onewallet.presentation.portfolio.allocation.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.composables.charts.composables.ChartLegend
import com.davidcrespo.onewallet.core.composables.charts.composables.ChartSequentialAnimation
import com.davidcrespo.onewallet.core.composables.charts.models.AssetSlice
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWBalance
import com.davidcrespo.onewallet.presentation.designsystem.composables.auxiliar.SectionType
import kotlinx.collections.immutable.ImmutableList

val ChartSize = 250.dp
val ChartStrokeWidth = 28.dp

@Composable
fun Graphic(
    portfolioItems: ImmutableList<AssetSlice>,
    totalBalance: Double,
    previousBalance: Double,
    currency: Currency,
    isBalanceVisible: Boolean,
    modifier: Modifier = Modifier,
    shouldAnimate: Boolean = true
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(ChartSize)
        ) {
            ChartSequentialAnimation(
                slices = portfolioItems,
                strokeWidth = ChartStrokeWidth,
                modifier = Modifier.fillMaxSize(),
                shouldAnimate = shouldAnimate
            )

            OWBalance(
                currency = currency,
                balance = totalBalance,
                previousBalance = previousBalance,
                isBalanceVisible = isBalanceVisible,
                isExpanded = true,//TODO***
                section = SectionType.ALLOCATION,
                modifier = Modifier
                    .size(ChartSize - ChartStrokeWidth),
                shouldAnimate = shouldAnimate
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ChartLegend(
            portfolioItems = portfolioItems,
            modifier = Modifier.padding(16.dp)
        )
    }
}
