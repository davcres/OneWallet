package com.davidcrespo.onewallet.presentation.portfolio

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidcrespo.onewallet.core.composables.ErrorBanner
import com.davidcrespo.onewallet.core.composables.animations.pulse
import com.davidcrespo.onewallet.core.extensions.applyIf
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWFloatingActionButton
import com.davidcrespo.onewallet.presentation.designsystem.theme.OneWalletTheme
import com.davidcrespo.onewallet.presentation.portfolio.components.EmptyInvestments
import com.davidcrespo.onewallet.presentation.portfolio.components.Header
import com.davidcrespo.onewallet.presentation.portfolio.components.SegmentedTabs
import com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.addInvestment.AddFundBottomSheet
import com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.addInvestment.AddInvestmentBottomSheet
import com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.addInvestment.AssetType
import com.davidcrespo.onewallet.presentation.portfolio.components.dialogs.BankDepositDialog
import com.davidcrespo.onewallet.presentation.portfolio.components.dialogs.StockDetailDialog
import com.davidcrespo.onewallet.presentation.portfolio.models.PortfolioTab
import com.davidcrespo.onewallet.presentation.portfolio.positions.PositionsTab
import com.davidcrespo.onewallet.presentation.portfolio.prices.PricesTab
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun PortfolioRoot(
    navigateToHistorical: () -> Unit,
    navigateToMarket: (isCrypto: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PortfolioViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PortfolioScreen(
        uiState = uiState,
        onAction = { action ->
            when(action) {
                is PortfolioIntent.NavigateToHistorical -> navigateToHistorical()
                is PortfolioIntent.NavigateToMarket -> navigateToMarket(action.isCrypto)
                else -> viewModel.handleIntent(action)
            }
        },
        modifier = modifier
    )
}

@Composable
private fun PortfolioScreen(
    uiState: PortfolioUiState,
    onAction: (PortfolioIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = remember { PortfolioTab.entries }
    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.portfolioItems) {
        onAction(PortfolioIntent.UpdateBalance)
    }

    var fabButtonExpanded by remember { mutableStateOf(false) }

    val blurRadius by animateDpAsState(
        targetValue = if (fabButtonExpanded || uiState.isFundDialogVisible) 16.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 1000
        ),
        label = "blur"
    )

    val overlayAlpha by animateFloatAsState(
        targetValue = if (fabButtonExpanded || uiState.isFundDialogVisible) 0.32f else 0f,
        label = "overlay"
    )

    Scaffold(
        floatingActionButton = {
            OWFloatingActionButton(
                expanded = fabButtonExpanded,
                onExpandedChange = { fabButtonExpanded = it },
                modifier = Modifier
                    .applyIf(uiState.portfolioItems.isEmpty()) { pulse() }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(blurRadius)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .wrapContentSize(),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    uiState.portfolioItems.isEmpty() -> {
                        EmptyInvestments(
                            modifier = Modifier.fillMaxSize().padding(16.dp)
                        )
                    }
                    else -> {
                        Header(
                            currency = uiState.selectedCurrency,
                            onCurrencyChange = { onAction(PortfolioIntent.ChangeCurrency) },
                            navigateToHistorical = { onAction(PortfolioIntent.NavigateToHistorical) },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        SegmentedTabs(
                            selectedIndex = pagerState.currentPage,
                            titles = tabs.toList(),
                            onSelected = { scope.launch { pagerState.animateScrollToPage(it) } },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxSize()
                        ) { page ->
                            when (tabs[page]) {
                                PortfolioTab.PORTFOLIO -> PositionsTab(
                                    currency = uiState.selectedCurrency,
                                    totalBalance = uiState.totalBalance,
                                    previousBalance = uiState.previousBalance,
                                    portfolioItems = uiState.portfolioItems,
                                    onRemoveItem = { onAction(PortfolioIntent.RemoveItem(it)) },
                                    onEditQuantity = { onAction(PortfolioIntent.EditQuantity(it)) }
                                )
                                PortfolioTab.PRICES -> PricesTab(
                                    currency = uiState.selectedCurrency,
                                    portfolioItems = uiState.portfolioItems,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }

        if (fabButtonExpanded || uiState.isFundDialogVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = overlayAlpha))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        fabButtonExpanded = false
                    }
            )
        }

        if (fabButtonExpanded) {
            AddInvestmentBottomSheet(
                visible = fabButtonExpanded,
                onDismiss = { fabButtonExpanded = false },
                onAssetTypeClick = { asset ->
                    when (asset) {
                        AssetType.Stock -> onAction(PortfolioIntent.NavigateToMarket(false))
                        AssetType.Crypto -> onAction(PortfolioIntent.NavigateToMarket(true))
                        AssetType.Fund -> onAction(PortfolioIntent.ShowFundDialog)
                        AssetType.Bank -> onAction(PortfolioIntent.ShowBankDialog)
                    }
                    fabButtonExpanded = false
                }
            )
        }

        // Edit Quantity Dialog
        uiState.editingItem?.let { item ->
            StockDetailDialog(
                item = item,
                onDismiss = { onAction(PortfolioIntent.EditQuantity(null)) },
                onConfirmQuantity = { quantity ->
                    onAction(PortfolioIntent.UpdateQuantity(item, quantity))
                }
            )
        }

        // Add Bank/Deposit Dialog
        if (uiState.isBankDialogVisible) {
            BankDepositDialog(
                onDismiss = { onAction(PortfolioIntent.DismissBankDialog) },
                onConfirm = { name, amount ->
                    onAction(PortfolioIntent.AddBankItem(name, amount))
                }
            )
        }

        // Add Fund/ETF Dialog
        AddFundBottomSheet(
            visible = uiState.isFundDialogVisible,
            onDismiss = { onAction(PortfolioIntent.DismissFundDialog) },
            onAddFund = { isin, quantity ->
                onAction(PortfolioIntent.AddFundItem(isin, quantity))
            },
            onIsinError = { isinError ->
                onAction(PortfolioIntent.SetError(isinError ?: "Desafortunadamente no hemos podido obtener el fondo.\nPrueba con otro ISIN."))
            }
        )

        ErrorBanner(
            message = uiState.error,
            autoCloseable = true,
            showCloseIcon = false,
            onErrorDismiss = { onAction(PortfolioIntent.ClearError) },
        )
    }
}

@Preview
@Composable
private fun PortfolioScreenPreview() {
    OneWalletTheme {
        PortfolioScreen(
            uiState = PortfolioUiState(
                portfolioItems = emptyList(),
                symbolsWithPrice = listOf("AAPL"),
                usdEurRate = 1.0,
                totalBalance = 10.0,
                previousBalance = 9.0,
                editingItem = null,
                isFundDialogVisible = false,
                isBankDialogVisible = false,
                isLoading = false,
                error = null
            ),
            onAction = {}
        )
    }
}
