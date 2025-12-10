package com.davidcrespo.onewallet.presentation.screens

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.davidcrespo.onewallet.presentation.contract.PriceIntent
import com.davidcrespo.onewallet.presentation.viewmodels.PriceViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceScreen(
    modifier: Modifier = Modifier,
    viewModel: PriceViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    var draggingItemIndex by remember { mutableStateOf<Int?>(null) }
    var draggingItemOffset by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        viewModel.handleIntent(PriceIntent.LoadInitialData)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = {
                    viewModel.handleIntent(PriceIntent.SearchQueryChanged(it))
                    expanded = true
                },
                label = { Text("Buscar símbolo") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            if (uiState.filteredSymbols.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    uiState.filteredSymbols.take(50).forEach { symbol ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = symbol.displaySymbol,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = symbol.description,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            },
                            onClick = {
                                viewModel.handleIntent(PriceIntent.SelectSymbol(symbol))
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        HorizontalDivider()

        Text(
            text = "Elementos Seleccionados:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(items = uiState.selectedSymbols, key = { _, item -> item.displaySymbol }) { index, stock ->
                val currentItemIndex by rememberUpdatedState(index)
                val isDragging = index == draggingItemIndex
                val zIndex = if (isDragging) 1f else 0f
                val scale = if (isDragging) 1.05f else 1f

                ListItem(
                    headlineContent = { Text(stock.displaySymbol) },
                    supportingContent = { Text(stock.description) },
                    trailingContent = { Text(stock.currency) },
                    tonalElevation = if (isDragging) 8.dp else 2.dp,
                    shadowElevation = if (isDragging) 8.dp else 2.dp,
                    modifier = Modifier
                        .zIndex(zIndex)
                        .graphicsLayer {
                            translationY = if (isDragging) draggingItemOffset else 0f
                            scaleX = scale
                            scaleY = scale
                        }
                        .pointerInput(Unit) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    draggingItemIndex = currentItemIndex
                                    draggingItemOffset = 0f
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    draggingItemOffset += dragAmount.y

                                    val currentDraggingIndex = draggingItemIndex ?: return@detectDragGesturesAfterLongPress
                                    val currentItemInfo = listState.layoutInfo.visibleItemsInfo
                                        .find { it.index == currentDraggingIndex } ?: return@detectDragGesturesAfterLongPress

                                    val currentItemCenter = currentItemInfo.offset + currentItemInfo.size / 2
                                    val dragOffsetAbsolute = currentItemCenter + draggingItemOffset

                                    // Find the item we are hovering over
                                    val targetItem = listState.layoutInfo.visibleItemsInfo.find { itemInfo ->
                                        itemInfo.index != currentDraggingIndex &&
                                                dragOffsetAbsolute > itemInfo.offset &&
                                                dragOffsetAbsolute < (itemInfo.offset + itemInfo.size)
                                    }

                                    if (targetItem != null) {
                                        val targetIndex = targetItem.index
                                        // Trigger the move
                                        viewModel.handleIntent(PriceIntent.MoveSymbol(currentDraggingIndex, targetIndex))
                                        // Update the tracking index to the new position
                                        draggingItemIndex = targetIndex
                                        
                                        val distance = targetItem.offset - currentItemInfo.offset
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
            }
        }
    }
}
