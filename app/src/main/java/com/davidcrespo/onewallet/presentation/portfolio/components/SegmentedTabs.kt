package com.davidcrespo.onewallet.presentation.portfolio.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.presentation.designsystem.theme.ItemBackground
import com.davidcrespo.onewallet.presentation.portfolio.models.PortfolioTab
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun SegmentedTabs(
    selectedIndex: Int,
    titles: ImmutableList<PortfolioTab>,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 72.dp
) {
    require(titles.size >= 2) { "SegmentedTabs needs at least 2 tabs" }

    BoxWithConstraints(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(2.dp, ItemBackground, CircleShape)
            .padding(8.dp)
    ) {
        val segmentWidth = maxWidth / titles.size
        val targetOffset = segmentWidth * selectedIndex
        val offsetX by animateDpAsState(targetValue = targetOffset, label = "indicatorOffset")

        // Indicator (la “selección” interior)
        Box(
            modifier = Modifier
                .offset(x = offsetX)
                .width(segmentWidth)
                .fillMaxSize()
                .clip(CircleShape)
                .background(ItemBackground)
        )

        // Click areas + text
        Row(Modifier.fillMaxSize()) {
            titles.forEachIndexed { index, title ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clip(CircleShape)
                        .clickable { onSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(title.title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (index == selectedIndex) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PortfolioSegmentedTabsPreview() {
    SegmentedTabs(
        selectedIndex = 0,
        titles = PortfolioTab.entries.toImmutableList(),
        onSelected = {},
        modifier = Modifier.padding(16.dp)
    )
}
