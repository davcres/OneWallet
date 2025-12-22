package com.davidcrespo.onewallet.presentation.portfolio.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.davidcrespo.onewallet.domain.model.investment.Investment
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioList(
    items: List<Investment>,
    onMove: (Int, Int) -> Unit,
    onRemove: (Investment) -> Unit,
    onEdit: (Investment) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    var draggingItemIndex by remember { mutableStateOf<Int?>(null) }
    var draggingItemOffset by remember { mutableFloatStateOf(0f) }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(items = items, key = { _, item -> item.symbol }) { index, portfolioItem ->
            var visible by remember { mutableStateOf(false) }

            // Animation with waterfall effect
            LaunchedEffect(Unit) {
                delay(index * 100L)
                visible = true
            }

            val currentItemIndex by rememberUpdatedState(index)
            val isDragging = index == draggingItemIndex
            val zIndex = if (isDragging) 1f else 0f
            val scale = if (isDragging) 1.05f else 1f

            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = {
                    if (it == SwipeToDismissBoxValue.EndToStart) {
                        onRemove(portfolioItem)
                        true
                    } else {
                        false
                    }
                }
            )

            AnimatedVisibility(
                visible = visible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it }
                ) + fadeIn()
            ) {
                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                            Color.Red.copy(alpha = 0.8f)
                        } else {
                            Color.Transparent
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .background(color)
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Borrar",
                                tint = Color.White
                            )
                        }
                    },
                    content = {
                        PortfolioItemCard(
                            item = portfolioItem,
                            modifier = Modifier
                                .graphicsLayer {
                                    translationY = if (isDragging) draggingItemOffset else 0f
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clickable { onEdit(portfolioItem) }
                                .pointerInput(Unit) {
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = {
                                            draggingItemIndex = currentItemIndex
                                            draggingItemOffset = 0f
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            draggingItemOffset += dragAmount.y

                                            val currentDraggingIndex = draggingItemIndex
                                                ?: return@detectDragGesturesAfterLongPress
                                            val currentItemInfo = listState.layoutInfo.visibleItemsInfo
                                                .find { it.index == currentDraggingIndex }
                                                ?: return@detectDragGesturesAfterLongPress

                                            val currentItemCenter =
                                                currentItemInfo.offset + currentItemInfo.size / 2
                                            val dragOffsetAbsolute =
                                                currentItemCenter + draggingItemOffset

                                            val targetItem =
                                                listState.layoutInfo.visibleItemsInfo.find { itemInfo ->
                                                    itemInfo.index != currentDraggingIndex &&
                                                            dragOffsetAbsolute > itemInfo.offset &&
                                                            dragOffsetAbsolute < (itemInfo.offset + itemInfo.size)
                                                }

                                            if (targetItem != null) {
                                                val targetIndex = targetItem.index
                                                onMove(currentDraggingIndex, targetIndex)
                                                draggingItemIndex = targetIndex
                                                val distance =
                                                    targetItem.offset - currentItemInfo.offset
                                                draggingItemOffset -= distance
                                            }
                                        },
                                        onDragEnd = {
                                            draggingItemIndex = null
                                            draggingItemOffset = 0f
                                        },
                                        onDragCancel = {
                                            draggingItemIndex = null
                                            draggingItemOffset = 0f
                                        }
                                    )
                                }
                        )
                    },
                    modifier = Modifier
                        .zIndex(zIndex)
                        .then(
                            if (draggingItemIndex == null) {
                                Modifier.animateItem()
                            } else {
                                Modifier
                            }
                        )
                )
            }
        }
    }
}
