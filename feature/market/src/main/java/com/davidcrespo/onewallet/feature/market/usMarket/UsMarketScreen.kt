package com.davidcrespo.onewallet.feature.market.usMarket

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidcrespo.onewallet.core.R
import com.davidcrespo.onewallet.domain.model.investment.MarketType
import com.davidcrespo.onewallet.core.designsystem.composables.OWLoader
import com.davidcrespo.onewallet.core.designsystem.theme.OneWalletTheme
import com.davidcrespo.onewallet.feature.market.components.MarketListItem
import com.davidcrespo.onewallet.feature.market.components.MarketSearchBar
import com.davidcrespo.onewallet.feature.market.usMarket.components.GlobalMarketsCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun UsMarketRoot(
    isCrypto: Boolean,
    navigateToGlobalMarket: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UsMarketViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.handleIntent(UsMarketIntent.LoadInitialData(isCrypto))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                UsMarketEffect.NavigateBack -> onBack()
                UsMarketEffect.NavigateToGlobalMarket -> navigateToGlobalMarket()
            }
        }
    }

    UsMarketScreen(
        uiState = uiState,
        onAction = viewModel::handleIntent,
        isCrypto = isCrypto,
        onBack = onBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UsMarketScreen(
    uiState: UsMarketUiState,
    onAction: (UsMarketIntent) -> Unit,
    isCrypto: Boolean,
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
                    text = if (isCrypto) stringResource(R.string.add_crypto_title) else stringResource(R.string.add_us_stocks_title),
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleMedium
                )

                if (uiState.assetsToSaveToPortfolio.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            onAction(UsMarketIntent.SaveAssetsSelected)
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
                    else -> {
                        MarketSearchBar(
                            isCrypto = isCrypto,
                            marketType = MarketType.US,
                            query = uiState.searchQuery,
                            onQueryChange = { onAction(UsMarketIntent.SearchQueryChanged(it)) },
                            onSearch = {
                                focusManager.clearFocus(force = true)
                                keyboardController?.hide()
                                onAction(UsMarketIntent.SearchQueryChanged(it))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )

                        LazyColumn(modifier = Modifier) {
                            uiState.filteredAssets.forEach { (letter, assets) ->
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
                                        marketType = MarketType.US,
                                        marketAsset = asset,
                                        isSelected = uiState.assetsToSaveToPortfolio.find { it.symbol == asset.symbol } != null,
                                        addOneAsset = {
                                            focusManager.clearFocus(force = true)
                                            keyboardController?.hide()
                                            onAction(UsMarketIntent.AddOneAsset(asset))
                                        },
                                        selectAsset = {
                                            focusManager.clearFocus(force = true)
                                            keyboardController?.hide()
                                            onAction(UsMarketIntent.SelectAsset(asset))
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = uiState.showGlobalMarketsCard && !uiState.isCrypto,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = spring(dampingRatio = 0.5f, stiffness = 350f)
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight }
                ) + fadeOut(),
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
            ) {
                GlobalMarketsCard(
                    visible = uiState.showGlobalMarketsCard,
                    onOpenGlobalMarkets = { onAction(UsMarketIntent.OpenGlobalMarket) },
                    onClose = { onAction(UsMarketIntent.CloseGlobalMarketCard) },
                )
            }
        }
    }
}

@Preview
@Composable
private fun UsMarketScreenPreview() {
    OneWalletTheme {
        UsMarketScreen(
            uiState = UsMarketUiState(),
            onAction = {},
            isCrypto = false,
            onBack = {}
        )
    }
}