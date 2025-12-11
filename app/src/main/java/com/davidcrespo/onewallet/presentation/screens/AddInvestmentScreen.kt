package com.davidcrespo.onewallet.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.presentation.contract.PriceIntent
import com.davidcrespo.onewallet.presentation.contract.PriceUiState
import com.davidcrespo.onewallet.presentation.screens.components.StockListItem
import com.davidcrespo.onewallet.presentation.screens.components.StockSearchBar

@Composable
fun AddInvestmentScreen(
    uiState: PriceUiState,
    onIntent: (PriceIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    IconButton(onClick = { onIntent(PriceIntent.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                    
                    Box(modifier = Modifier.weight(1f)) {
                        StockSearchBar(
                            query = uiState.searchQuery,
                            onQueryChange = { onIntent(PriceIntent.SearchQueryChanged(it)) },
                            filteredSymbols = emptyList(), // We don't want the dropdown here, we use the main list
                            onSymbolSelected = {}
                        )
                    }
                }
                HorizontalDivider()
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(uiState.filteredSymbols) { symbol ->
                StockListItem(
                    stock = symbol,
                    onClick = { onIntent(PriceIntent.SelectSymbol(symbol)) }
                )
                HorizontalDivider()
            }
        }
    }
}