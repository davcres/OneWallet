package com.davidcrespo.onewallet.feature.portfolio.components.segmentedButton

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.designsystem.theme.OneWalletTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun SegmentedButton(
    selectedIndex: Int,
    items: ImmutableList<SegmentedItem>,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 72.dp
) {
    require(items.size >= 2) { "SegmentedTabs needs at least 2 tabs" }

    BoxWithConstraints(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(2.dp, MaterialTheme.colorScheme.onTertiaryContainer, CircleShape)
            .padding(8.dp)
    ) {
        val segmentWidth = maxWidth / items.size
        val targetOffset = segmentWidth * selectedIndex
        val offsetX by animateDpAsState(targetValue = targetOffset, label = "indicatorOffset")

        Box(
            modifier = Modifier
                .offset(x = offsetX)
                .width(segmentWidth)
                .fillMaxSize()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )

        Row(Modifier.fillMaxSize()) {
            items.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex
                val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clip(CircleShape)
                        .clickable { onSelected(index) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = contentColor
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PortfolioSegmentedButtonPreview() {
    OneWalletTheme {
        SegmentedButton(
            selectedIndex = 0,
            items = persistentListOf(
                SegmentedItem(0, "Tipo", Icons.Default.PieChart),
                SegmentedItem(1, "Categoria", Icons.AutoMirrored.Filled.Label),
            ),
            onSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
