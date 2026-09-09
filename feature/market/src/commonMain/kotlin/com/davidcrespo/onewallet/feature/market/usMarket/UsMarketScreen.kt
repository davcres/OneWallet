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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidcrespo.onewallet.core.generated.resources.Res
import com.davidcrespo.onewallet.core.generated.resources.add_count_action
import com.davidcrespo.onewallet.core.generated.resources.add_crypto_title
import com.davidcrespo.onewallet.core.generated.resources.add_us_stocks_title
import com.davidcrespo.onewallet.core.generated.resources.cancel_action
import com.davidcrespo.onewallet.domain.model.investment.MarketType
import com.davidcrespo.onewallet.core.designsystem.composables.OWLoader
import com.davidcrespo.onewallet.core.designsystem.theme.OneWalletTheme
import com.davidcrespo.onewallet.feature.market.components.MarketListItem
import com.davidcrespo.onewallet.feature.market.components.MarketSearchBar
import com.davidcrespo.onewallet.feature.market.usMarket.components.GlobalMarketsCard
import org.koin.compose.viewmodel.koinViewModel

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
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isCrypto) stringResource(Res.string.add_crypto_title) else stringResource(Res.string.add_us_stocks_title),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    TextButton(
                        onClick = onBack
                    ) {
                        Text(stringResource(Res.string.cancel_action))
                    }
                },
                actions = {
                    val hasSelection = uiState.assetsToSaveToPortfolio.isNotEmpty()
                    TextButton(
                        onClick = {
                            if (hasSelection) {
                                onAction(UsMarketIntent.SaveAssetsSelected)
                            }
                        },
                        enabled = hasSelection,
                        modifier = Modifier
                            .alpha(if (hasSelection) 1f else 0f)
                            .then(if (!hasSelection) Modifier.clearAndSetSemantics { } else Modifier)
                    ) {
                        Text(
                            text = stringResource(
                                Res.string.add_count_action,
                                maxOf(uiState.assetsToSaveToPortfolio.size, 1)
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
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