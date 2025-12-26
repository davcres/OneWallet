package com.davidcrespo.onewallet.presentation.market

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.presentation.market.components.MarketListItem
import com.davidcrespo.onewallet.presentation.market.components.MarketSearchBar
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                TextButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text("Cancelar")
                }

                Text(
                    text = if (isCrypto) "Añadir Criptomoneda" else "Añadir Acciones",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleMedium
                )

                if (uiState.assetsToSaveToPortfolio.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            viewModel.handleIntent(MarketIntent.SaveAssetsSelected)
                        },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text("Añadir (${uiState.assetsToSaveToPortfolio.size})")
                    }
                }
            }
        },
        modifier = modifier
            .fillMaxSize()
            .imePadding() // evitar que el teclado en pantalla tape tu contenido
    ) { paddingValues ->
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) {
                    detectTapGestures {
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                    }
                }
        ) {
            MarketSearchBar(
                isCrypto = isCrypto,
                query = uiState.searchQuery,
                onQueryChange = { viewModel.handleIntent(MarketIntent.SearchQueryChanged(it)) },
                onSearch = {
                    focusManager.clearFocus(force = true)
                    keyboardController?.hide()
                    viewModel.handleIntent(MarketIntent.SearchQueryChanged(it))
                },
                onClearQuery = {
                    viewModel.handleIntent(MarketIntent.SearchQueryChanged(""))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            LazyColumn(modifier = Modifier) {
                uiState.filteredAssets.forEach { (letter, assets) ->

                    stickyHeader(key = "header-$letter") {
                        Text(
                            text = letter.toString(),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(16.dp)
                        )
                    }

                    items(
                        items = assets,
                        key = { asset -> letter + asset.symbol }
                    ) { asset ->
                        MarketListItem(
                            marketAsset = asset,
                            isSelected = uiState.assetsToSaveToPortfolio.contains(asset),
                            addOneAsset = {
                                focusManager.clearFocus(force = true)
                                keyboardController?.hide()
                                viewModel.handleIntent(MarketIntent.AddOneAsset(asset))
                            },
                            selectAsset = {
                                focusManager.clearFocus(force = true)
                                keyboardController?.hide()
                                viewModel.handleIntent(MarketIntent.SelectAsset(asset))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}