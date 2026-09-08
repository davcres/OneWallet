package com.davidcrespo.onewallet.core.designsystem.composables.auxiliar.owDropdownSelector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun OWDropdownSelectorPopup(
    items: ImmutableList<DropdownItem>,
    selectedItem: DropdownItem?,
    onDismissRequest: () -> Unit,
    onItemClicked: (DropdownItem) -> Unit,
    onCustomItemClicked: () -> Unit,
    headerSize: IntSize,
    animationState: MutableTransitionState<Boolean>,
    onItemSizeChanged: (IntSize) -> Unit,
    maxListHeight: Dp,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val isAnimatingOrExpanded = animationState.targetState || animationState.currentState

    if (isAnimatingOrExpanded) {
        val dropdownWidth = with(density) { headerSize.width.toDp() }
        
        Popup(
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(
                focusable = true,
                dismissOnClickOutside = true,
                dismissOnBackPress = true
            ),
            offset = IntOffset(0, headerSize.height + with(density) { 8.dp.roundToPx() })
        ) {
            AnimatedVisibility(
                visibleState = animationState,
                enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
            ) {
                val dropdownShape = RoundedCornerShape(24.dp)

                Column(
                    modifier = modifier
                        .width(dropdownWidth)
                        .clip(dropdownShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .border(
                            width = 1.5.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = dropdownShape
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .heightIn(max = maxListHeight)
                            .verticalScroll(rememberScrollState())
                    ) {
                        items.forEachIndexed { index, item ->
                            OWDropdownSelectorItem(
                                text = item.name,
                                selected = item == selectedItem,
                                onClick = { onItemClicked(item) },
                                modifier = Modifier.onSizeChanged { size ->
                                    if (index == 0 && size.height > 0) {
                                        onItemSizeChanged(size)
                                    }
                                }
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))

                    OWDropdownSelectorCustomItem(
                        onClick = onCustomItemClicked
                    )
                }
            }
        }
    }
}
