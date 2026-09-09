package com.davidcrespo.onewallet.feature.market.globalMarket

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
import com.davidcrespo.onewallet.core.generated.resources.add_global_stocks_title
import com.davidcrespo.onewallet.core.generated.resources.cancel_action
import com.davidcrespo.onewallet.core.generated.resources.global_markets_no_results
import com.davidcrespo.onewallet.core.generated.resources.global_markets_try_again
import com.davidcrespo.onewallet.core.generated.resources.global_markets_warning
import com.davidcrespo.onewallet.core.composables.Button
import com.davidcrespo.onewallet.core.composables.auxiliar.ButtonStyle
import com.davidcrespo.onewallet.domain.model.investment.MarketType
import com.davidcrespo.onewallet.core.designsystem.composables.OWLoader
import com.davidcrespo.onewallet.core.designsystem.theme.OneWalletTheme
import com.davidcrespo.onewallet.feature.market.components.MarketListItem
import com.davidcrespo.onewallet.feature.market.components.MarketSearchBar
import org.koin.compose.viewmodel.koinViewModel

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
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.add_global_stocks_title),
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
                                onAction(GlobalMarketIntent.SaveAssetsSelected)
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
                            text = stringResource(Res.string.global_markets_no_results),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(Res.string.global_markets_warning),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            text = stringResource(Res.string.global_markets_try_again),
                            contentDescription = stringResource(Res.string.global_markets_try_again),
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
                                text = stringResource(Res.string.global_markets_warning),
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