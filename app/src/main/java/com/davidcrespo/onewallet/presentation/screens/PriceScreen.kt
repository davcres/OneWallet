package com.davidcrespo.onewallet.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.presentation.contract.PriceIntent
import com.davidcrespo.onewallet.presentation.screens.components.PortfolioList
import com.davidcrespo.onewallet.presentation.screens.components.QuantityDialog
import com.davidcrespo.onewallet.presentation.screens.components.StockSearchBar
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
                    .clickable(enabled = false) {}, // Block clicks
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