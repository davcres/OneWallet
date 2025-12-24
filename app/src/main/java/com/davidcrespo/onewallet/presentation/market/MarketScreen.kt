package com.davidcrespo.onewallet.presentation.market

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.core.composables.AnimatedList
import com.davidcrespo.onewallet.presentation.market.components.MarketListItem
import com.davidcrespo.onewallet.presentation.market.components.MarketSearchBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun MarketScreen(
    isCrypto: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MarketViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(MarketIntent.LoadInitialData(isCrypto))
    }

    if (uiState.navigateBack) {
        onBack()
    }

    Scaffold(
        topBar = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                    
                    Box(modifier = Modifier.weight(1f)) {
                        MarketSearchBar(
                            query = uiState.searchQuery,
                            onQueryChange = { viewModel.handleIntent(MarketIntent.SearchQueryChanged(it)) }
                        )
                    }
                }
                HorizontalDivider()
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        AnimatedList(
            items = uiState.filteredAssets,
            key = { it.symbol },
            modifier = modifier.fillMaxSize().padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            itemContent = { modifier, marketAsset ->
                MarketListItem(
                    marketAsset = marketAsset,
                    onClick = { viewModel.handleIntent(MarketIntent.SelectAsset(marketAsset)) },
                    modifier = modifier
                )

                HorizontalDivider(
                    //modifier = modifier
                )
            }
        )

    }
}