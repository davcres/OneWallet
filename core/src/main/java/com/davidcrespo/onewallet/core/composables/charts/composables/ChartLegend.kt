package com.davidcrespo.onewallet.core.composables.charts.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.composables.Dot
import com.davidcrespo.onewallet.core.composables.charts.models.AssetSlice
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ChartLegend(
    portfolioItems: ImmutableList<AssetSlice>,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    val maxPerRow = remember(portfolioItems.size) {
        if (portfolioItems.size <= 3) portfolioItems.size
        else (portfolioItems.size + 1) / 2
    }

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        maxItemsInEachRow = maxPerRow
    ) {
        portfolioItems.forEach { type ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Dot(
                    size = 12,
                    color = type.color
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = type.name,
                    style = style,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
