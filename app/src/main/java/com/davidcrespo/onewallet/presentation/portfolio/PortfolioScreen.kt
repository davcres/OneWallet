package com.davidcrespo.onewallet.presentation.portfolio

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.core.composables.ErrorBanner
import com.davidcrespo.onewallet.core.composables.modifiers.animations.pulse
import com.davidcrespo.onewallet.core.extensions.applyIf
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWFloatingActionButton
import com.davidcrespo.onewallet.presentation.designsystem.theme.OneWalletTheme
import com.davidcrespo.onewallet.presentation.portfolio.allocation.AllocationTab
import com.davidcrespo.onewallet.presentation.portfolio.components.EmptyInvestments
import com.davidcrespo.onewallet.presentation.portfolio.components.Header
import com.davidcrespo.onewallet.presentation.portfolio.components.SegmentedTabs
import com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.addInvestment.AddBankBottomSheet
import com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.addInvestment.AddFundBottomSheet
import com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.addInvestment.AddInvestmentBottomSheet
import com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.addInvestment.AssetType
import com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.deleteInvestment.DeleteInvestmentBottomSheet
import com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.updateInvestment.UpdateInvestmentBottomSheet
import com.davidcrespo.onewallet.presentation.portfolio.models.PortfolioTab
import com.davidcrespo.onewallet.presentation.portfolio.positions.PositionsTab
import com.davidcrespo.onewallet.presentation.portfolio.prices.PricesTab
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
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
    var fabButtonExpanded by remember { mutableStateOf(false) }

    val hideBackground =
        uiState.isFundDialogVisible ||
                uiState.isEtfDialogVisible ||
                uiState.isBankDialogVisible ||
                uiState.isOtherDialogVisible ||
                uiState.editingItem != null ||
                uiState.deletingItem != null ||
                fabButtonExpanded

    val blurRadius by animateDpAsState(
        targetValue = if (hideBackground) 16.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 1000
        ),
        label = "blur"
    )

    val overlayAlpha by animateFloatAsState(
        targetValue = if (hideBackground) 0.32f else 0f,
        label = "overlay"
    )

    LaunchedEffect(uiState.portfolioItems) {
        onAction(PortfolioIntent.UpdateBalance)
    }

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
                Header(
                    currency = uiState.selectedCurrency,
                    onCurrencyChange = { onAction(PortfolioIntent.ChangeCurrency) },
                    navigateToHistorical = { onAction(PortfolioIntent.NavigateToHistorical) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

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
                        SegmentedTabs(
                            selectedIndex = pagerState.currentPage,
                            titles = tabs.toPersistentList(),
                            onSelected = { scope.launch { pagerState.animateScrollToPage(it) } },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxSize()
                        ) { page ->
                            when (tabs[page]) {
                                PortfolioTab.ALLOCATION -> AllocationTab(
                                    portfolioItems = uiState.portfolioItems,
                                    modifier = Modifier.fillMaxSize()
                                )
                                PortfolioTab.PORTFOLIO -> PositionsTab(
                                    currency = uiState.selectedCurrency,
                                    totalBalance = uiState.totalBalance,
                                    previousBalance = uiState.previousBalance,
                                    portfolioItems = uiState.portfolioItems,
                                    onRemoveItem = { onAction(PortfolioIntent.ShowDeleteDialog(it)) },
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

        if (hideBackground) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = overlayAlpha))
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
                        AssetType.ETF -> onAction(PortfolioIntent.ShowEtfDialog)
                        AssetType.Bank -> onAction(PortfolioIntent.ShowBankDialog)
                        AssetType.Other -> onAction(PortfolioIntent.ShowOtherDialog)
                    }
                    fabButtonExpanded = false
                }
            )
        }

        // Edit Quantity Dialog
        uiState.editingItem?.let { item ->
            UpdateInvestmentBottomSheet(
                investment = item,
                currency = uiState.selectedCurrency,
                visible = true,
                onDismiss = { onAction(PortfolioIntent.EditQuantity(null)) },
                onEditInvestment = { quantity ->
                    onAction(PortfolioIntent.UpdateQuantity(item, quantity))
                },
                onQuantityError = { quantityError ->
                    onAction(PortfolioIntent.SetError(quantityError))
                }
            )
        }

        // Delete Investment Dialog
        uiState.deletingItem?.let { item ->
            DeleteInvestmentBottomSheet(
                investment = item,
                visible = true,
                onDismiss = { onAction(PortfolioIntent.ShowDeleteDialog(null)) },
                onDelete = { onAction(PortfolioIntent.RemoveItem(item)) }
            )
        }

        // Add Bank/Deposit Dialog
        AddBankBottomSheet(
            visible = uiState.isBankDialogVisible,
            currency = uiState.selectedCurrency,
            isBank = true,
            onDismiss = { onAction(PortfolioIntent.DismissBankDialog) },
            onAddBank = { name, amount, currency ->
                onAction(PortfolioIntent.AddBankItem(name, amount, currency))
            }
        )

        // Add Other Dialog
        AddBankBottomSheet(
            visible = uiState.isOtherDialogVisible,
            currency = uiState.selectedCurrency,
            isBank = false,
            onDismiss = { onAction(PortfolioIntent.DismissOtherDialog) },
            onAddBank = { name, amount, currency ->
                onAction(PortfolioIntent.AddOtherItem(name, amount, currency))
            }
        )

        val defaultFundError = stringResource(R.string.fund_fetch_error)
        val defaultEtfError = stringResource(R.string.etf_fetch_error)


        // Add Fund Dialog
        AddFundBottomSheet(
            visible = uiState.isFundDialogVisible,
            isFund = true,
            onDismiss = { onAction(PortfolioIntent.DismissFundDialog) },
            onAddFund = { isin, quantity ->
                onAction(PortfolioIntent.AddFundItem(isin, quantity))
            },
            onIsinError = { isinError ->
                onAction(PortfolioIntent.SetError(isinError ?: defaultFundError))
            }
        )

        // Add ETF Dialog
        AddFundBottomSheet(
            visible = uiState.isEtfDialogVisible,
            isFund = false,
            onDismiss = { onAction(PortfolioIntent.DismissEtfDialog) },
            onAddFund = { isin, quantity ->
                onAction(PortfolioIntent.AddEtfItem(isin, quantity))
            },
            onIsinError = { isinError ->
                onAction(PortfolioIntent.SetError(isinError ?: defaultEtfError))
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
                portfolioItems = persistentListOf(),
                symbolsWithPrice = persistentListOf("AAPL"),
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
