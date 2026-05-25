package com.davidcrespo.onewallet.presentation.market.globalMarket

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.core.composables.Button
import com.davidcrespo.onewallet.core.composables.auxiliar.ButtonStyle
import com.davidcrespo.onewallet.domain.model.investment.MarketType
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWLoader
import com.davidcrespo.onewallet.presentation.designsystem.theme.OneWalletTheme
import com.davidcrespo.onewallet.presentation.market.components.MarketListItem
import com.davidcrespo.onewallet.presentation.market.components.MarketSearchBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun GlobalMarketRoot(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GlobalMarketViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                GlobalMarketEffect.NavigateBack -> onBack()
            }
        }
    }

    GlobalMarketScreen(
        uiState = uiState,
        onAction = viewModel::handleIntent,
        onBack = onBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GlobalMarketScreen(
    uiState: GlobalMarketUiState,
    onAction: (GlobalMarketIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding() // Necesaria para no solapar al no utilizar una TopAppBar estandar de material3
                    .padding(16.dp)
            ) {
                TextButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text(stringResource(R.string.cancel_action))
                }

                Text(
                    text = stringResource(R.string.add_global_stocks_title),
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleMedium
                )

                if (uiState.assetsToSaveToPortfolio.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            onAction(GlobalMarketIntent.SaveAssetsSelected)
                        },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text(stringResource(R.string.add_count_action, uiState.assetsToSaveToPortfolio.size))
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            focusManager.clearFocus(force = true)
                            keyboardController?.hide()
                        }
                    }
            ) {
                when {
                    uiState.isLoading -> {
                        OWLoader(
                            modifier = Modifier.weight(1f)
                        )
                    }
                    uiState.error != null -> {
                        Text(
                            text = stringResource(uiState.error),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                    }
                    uiState.marketAssets?.firstOrNull()?.second?.isEmpty() == true && uiState.searchQuery.isNotEmpty() -> {
                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = stringResource(R.string.global_markets_no_results),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(R.string.global_markets_warning),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            text = stringResource(R.string.global_markets_try_again),
                            contentDescription = stringResource(R.string.global_markets_try_again),
                            style = ButtonStyle.SECONDARY,
                            onClick = {
                                onAction(GlobalMarketIntent.RetrySearch)
                            }
                        )

                        Spacer(modifier = Modifier.weight(1f))
                    }
                    else -> {
                        MarketSearchBar(
                            isCrypto = false,
                            marketType = MarketType.GLOBAL,
                            query = uiState.searchQuery,
                            onQueryChange = { onAction(GlobalMarketIntent.OnQueryChange(it)) },
                            onSearch = {
                                focusManager.clearFocus(force = true)
                                keyboardController?.hide()
                                onAction(GlobalMarketIntent.SearchByQuery(it))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )

                        AnimatedVisibility(
                            visible = uiState.marketAssets == null || uiState.marketAssets.firstOrNull()?.second?.isEmpty() == true
                        ) {
                            Text(
                                text = stringResource(R.string.global_markets_warning),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(modifier = Modifier) {
                            uiState.marketAssets?.forEach { (letter, assets) ->
                                stickyHeader(key = "header-$letter") {
                                    Text(
                                        text = letter,
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
                                        marketType = MarketType.GLOBAL,
                                        marketAsset = asset,
                                        isSelected = uiState.assetsToSaveToPortfolio.find { it.symbol == asset.symbol } != null,
                                        addOneAsset = {
                                            focusManager.clearFocus(force = true)
                                            keyboardController?.hide()
                                            onAction(GlobalMarketIntent.AddOneAsset(asset))
                                        },
                                        selectAsset = {
                                            focusManager.clearFocus(force = true)
                                            keyboardController?.hide()
                                            onAction(GlobalMarketIntent.SelectAsset(asset))
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun GlobalMarketScreenPreview() {
    OneWalletTheme {
        GlobalMarketScreen(
            uiState = GlobalMarketUiState(),
            onAction = {},
            onBack = {}
        )
    }
}