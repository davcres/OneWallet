package com.davidcrespo.onewallet.core.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A custom grid layout that ensures all items in the grid have the same height,
 * matching the height of the tallest item.
 *
 * It uses intrinsic measurements to pre-calculate the maximum height required
 * by any item given the column width constraints, and then forces all items
 * to be measured with that exact height.
 *
 * @param columns The number of columns in the grid.
 * @param modifier The modifier to be applied to the layout.
 * @param spacing The spacing between items both vertically and horizontally.
 * @param content The composable content containing the items to be displayed in the grid.
 */
@Composable
fun EqualHeightGrid(
    columns: Int,
    modifier: Modifier = Modifier,
    spacing: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val spacingPx = spacing.roundToPx()
        val totalSpacing = (columns - 1) * spacingPx
        val itemWidth = (constraints.maxWidth - totalSpacing) / columns

        // Encontrar la altura máxima usando Intrinsics sin medir realmente
        val maxHeight = measurables.maxOfOrNull { 
            it.maxIntrinsicHeight(itemWidth) 
        } ?: 0

        val itemConstraints = constraints.copy(
            minWidth = itemWidth,
            maxWidth = itemWidth,
            minHeight = maxHeight,
            maxHeight = maxHeight
        )

        // Medir solo una vez con la altura final calculada
        val placeables = measurables.map { it.measure(itemConstraints) }

        val rows = (placeables.size + columns - 1) / columns
        val totalHeight = rows * maxHeight + (rows - 1) * spacingPx

        layout(constraints.maxWidth, totalHeight) {
            placeables.forEachIndexed { index, placeable ->
                val row = index / columns
                val col = index % columns
                placeable.placeRelative(
                    x = col * (itemWidth + spacingPx),
                    y = row * (maxHeight + spacingPx)
                )
            }
        }
    }
}
