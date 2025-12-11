package com.davidcrespo.onewallet.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.davidcrespo.onewallet.domain.model.PortfolioItem
import com.davidcrespo.onewallet.domain.model.finnhub.StockInfo
import com.davidcrespo.onewallet.presentation.contract.PriceIntent
import com.davidcrespo.onewallet.presentation.viewmodels.PriceViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PriceScreen(
    modifier: Modifier = Modifier,
    viewModel: PriceViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(PriceIntent.LoadInitialData)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StockSearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.handleIntent(PriceIntent.SearchQueryChanged(it)) },
                filteredSymbols = uiState.filteredSymbols,
                onSymbolSelected = { viewModel.handleIntent(PriceIntent.SelectSymbol(it)) }
            )

            HorizontalDivider()

            Text(
                text = "Elementos Seleccionados:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            PortfolioList(
                items = uiState.portfolioItems,
                onMove = { from, to -> viewModel.handleIntent(PriceIntent.MoveSymbol(from, to)) },
                onRemove = { viewModel.handleIntent(PriceIntent.RemoveItem(it)) },
                onEdit = { viewModel.handleIntent(PriceIntent.EditQuantity(it)) }
            )
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        if (uiState.editingItem != null) {
            QuantityDialog(
                item = uiState.editingItem!!,
                onDismiss = { viewModel.handleIntent(PriceIntent.EditQuantity(null)) },
                onConfirm = { quantity ->
                    viewModel.handleIntent(PriceIntent.UpdateQuantity(uiState.editingItem!!, quantity))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StockSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    filteredSymbols: List<StockInfo>,
    onSymbolSelected: (StockInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                onQueryChange(it)
                expanded = true
            },
            label = { Text("Buscar símbolo") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Buscar"
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        if (filteredSymbols.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                filteredSymbols.take(50).forEach { symbol ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = symbol.displaySymbol.firstOrNull()?.toString() ?: "?",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = symbol.displaySymbol,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = symbol.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        },
                        onClick = {
                            onSymbolSelected(symbol)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PortfolioList(
    items: List<PortfolioItem>,
    onMove: (Int, Int) -> Unit,
    onRemove: (PortfolioItem) -> Unit,
    onEdit: (PortfolioItem) -> Unit,
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
        itemsIndexed(items = items, key = { _, item -> item.stockInfo.displaySymbol }) { index, portfolioItem ->
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

                                        val currentDraggingIndex = draggingItemIndex ?: return@detectDragGesturesAfterLongPress
                                        val currentItemInfo = listState.layoutInfo.visibleItemsInfo
                                            .find { it.index == currentDraggingIndex } ?: return@detectDragGesturesAfterLongPress

                                        val currentItemCenter = currentItemInfo.offset + currentItemInfo.size / 2
                                        val dragOffsetAbsolute = currentItemCenter + draggingItemOffset

                                        val targetItem = listState.layoutInfo.visibleItemsInfo.find { itemInfo ->
                                            itemInfo.index != currentDraggingIndex &&
                                                    dragOffsetAbsolute > itemInfo.offset &&
                                                    dragOffsetAbsolute < (itemInfo.offset + itemInfo.size)
                                        }

                                        if (targetItem != null) {
                                            val targetIndex = targetItem.index
                                            onMove(currentDraggingIndex, targetIndex)
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
                },
                modifier = Modifier.zIndex(zIndex)
            )
        }
    }
}

@Composable
fun PortfolioItemCard(
    item: PortfolioItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = item.stockInfo.displaySymbol.firstOrNull()?.toString() ?: "?",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.stockInfo.displaySymbol,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.stockInfo.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Text(
                        text = "x ${item.quantity}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = item.stockInfo.currency,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Icon(
                imageVector = Icons.Rounded.DragHandle,
                contentDescription = "Reorder",
                tint = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Composable
fun QuantityDialog(
    item: PortfolioItem,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var text by remember { mutableStateOf(item.quantity.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Cantidad") },
        text = {
            Column {
                Text(text = "Introduce la cantidad para ${item.stockInfo.displaySymbol}:")
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    text.toDoubleOrNull()?.let { onConfirm(it) }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
