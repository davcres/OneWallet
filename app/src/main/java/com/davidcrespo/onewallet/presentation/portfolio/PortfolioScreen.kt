package com.davidcrespo.onewallet.presentation.portfolio

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.core.composables.ErrorBanner
import com.davidcrespo.onewallet.core.composables.modifiers.animations.pulse
import com.davidcrespo.onewallet.core.composables.modifiers.privacyBlur
import com.davidcrespo.onewallet.core.extensions.applyIf
import com.davidcrespo.onewallet.core.models.ThemeMode
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWFloatingActionButton
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWLoader
import com.davidcrespo.onewallet.presentation.designsystem.composables.OWShakeListener
import com.davidcrespo.onewallet.presentation.designsystem.theme.OneWalletTheme
import com.davidcrespo.onewallet.presentation.history.HistoryTab
import com.davidcrespo.onewallet.presentation.models.CurrencyView
import com.davidcrespo.onewallet.presentation.models.InvestmentView
import com.davidcrespo.onewallet.presentation.portfolio.allocation.AllocationTab
import com.davidcrespo.onewallet.presentation.portfolio.components.EmptyInvestments
import com.davidcrespo.onewallet.presentation.portfolio.components.Header
import com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.addInvestment.AddBankBottomSheet
import com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.addInvestment.AddFundBottomSheet
import com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.addInvestment.AddInvestmentBottomSheet
import com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.deleteInvestment.DeleteInvestmentBottomSheet
import com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.investmentType.InvestmentTypeBottomSheet
import com.davidcrespo.onewallet.presentation.portfolio.components.bottomSheet.updateInvestment.UpdateInvestmentBottomSheet
import com.davidcrespo.onewallet.presentation.portfolio.models.PortfolioTabs
import com.davidcrespo.onewallet.presentation.portfolio.positions.PositionsTab
import com.davidcrespo.onewallet.presentation.portfolio.prices.PricesTab
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel

@Composable
fun PortfolioRoot(
    initialTab: PortfolioTabs,
    navigateToMarket: (isCrypto: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PortfolioViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.portfolioItems) {
        viewModel.handleIntent(PortfolioIntent.UpdateBalance)
        viewModel.handleIntent(PortfolioIntent.GetItemsByType)
    }

    PortfolioScreen(
        uiState = uiState,
        initialTab = initialTab,
        onAction = { action ->
            when (action) {
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
    initialTab: PortfolioTabs,
    onAction: (PortfolioIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = remember { PortfolioTabs.entries }
    var selectedTab by rememberSaveable(initialTab) { mutableStateOf(initialTab) }
    var isBalanceVisible by rememberSaveable { mutableStateOf(true) }
    var fabButtonExpanded by remember { mutableStateOf(false) }
    val stateHolder = rememberSaveableStateHolder()

    val hideBackground =
        uiState.isFundDialogVisible ||
                uiState.isEtfDialogVisible ||
                uiState.isBankDialogVisible ||
                uiState.isOtherDialogVisible ||
                uiState.editingItem != null ||
                uiState.deletingItem != null ||
                uiState.typeDetail != null ||
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

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        OWShakeListener(
            onShake = { isBalanceVisible = !isBalanceVisible }
        )
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
        bottomBar = {
            if (uiState.portfolioItems.isNotEmpty()) {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    )
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        tabs.forEach { tab ->
                            val selected = selectedTab == tab
                            val animatedScale by animateFloatAsState(
                                targetValue = if (selected) 1.2f else 1f,
                                animationSpec = tween(300, easing = LinearEasing),
                                label = "nav_item_scale"
                            )
                            NavigationBarItem(
                                selected = selected,
                                onClick = { selectedTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = stringResource(tab.title),
                                        modifier = Modifier.scale(animatedScale)
                                    )
                                },
                                label = {
                                    Text(
                                        text = stringResource(tab.title),
                                        modifier = Modifier.scale(animatedScale)
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Transparent,
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .privacyBlur(blurRadius)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Header(
                    text = stringResource(selectedTab.description),
                    currency = uiState.selectedCurrency,
                    themeMode = uiState.themeMode,
                    onCurrencyChange = { onAction(PortfolioIntent.ChangeCurrency) },
                    onChangeUIMode = { onAction(PortfolioIntent.ToggleTheme(it)) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                when {
                    uiState.isLoading -> {
                        OWLoader(
                            modifier = Modifier.weight(1f)
                        )
                    }
                    uiState.portfolioItems.isEmpty() -> {
                        EmptyInvestments(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                    }
                    else -> {
                        Crossfade(
                            targetState = selectedTab,
                            label = "tab_switch",
                            modifier = Modifier.weight(1f)
                        ) { tab ->
                            stateHolder.SaveableStateProvider(key = tab) {
                                when (tab) {
                                    PortfolioTabs.POSITIONS -> PositionsTab(
                                        currency = uiState.selectedCurrency,
                                        totalBalance = uiState.totalBalance,
                                        previousBalance = uiState.previousBalance,
                                        portfolioItems = uiState.portfolioItems,
                                        onRemoveItem = { onAction(PortfolioIntent.ShowDeleteDialog(it)) },
                                        onEditQuantity = { onAction(PortfolioIntent.EditQuantity(it)) },
                                        changeBalanceVisibility = { isBalanceVisible = !isBalanceVisible },
                                        isBalanceVisible = isBalanceVisible,
                                        isActivePage = true
                                    )
                                    PortfolioTabs.ALLOCATION -> AllocationTab(
                                        itemsByType = uiState.portfolioItemsByType,
                                        totalBalance = uiState.totalBalance,
                                        previousBalance = uiState.previousBalance,
                                        currency = uiState.selectedCurrency,
                                        onSelect = { onAction(PortfolioIntent.SelectInvestmentType(it)) },
                                        modifier = Modifier.fillMaxSize(),
                                        isBalanceVisible = isBalanceVisible,
                                        isActivePage = true
                                    )
                                    PortfolioTabs.PRICES -> PricesTab(
                                        currency = uiState.selectedCurrency,
                                        portfolioItems = uiState.portfolioItems,
                                        modifier = Modifier.fillMaxSize(),
                                        isBalanceVisible = isBalanceVisible,
                                        isActivePage = true
                                    )
                                    PortfolioTabs.HISTORY -> HistoryTab(
                                        isBalanceVisible = isBalanceVisible,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
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
                        InvestmentType.STOCK -> onAction(PortfolioIntent.NavigateToMarket(false))
                        InvestmentType.CRYPTO -> onAction(PortfolioIntent.NavigateToMarket(true))
                        InvestmentType.FUND -> onAction(PortfolioIntent.ShowFundDialog)
                        InvestmentType.ETF -> onAction(PortfolioIntent.ShowEtfDialog)
                        InvestmentType.BANK -> onAction(PortfolioIntent.ShowBankDialog)
                        InvestmentType.OTHER -> onAction(PortfolioIntent.ShowOtherDialog)
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
            isLoading = uiState.isLoadingBottomSheet,
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
            isLoading = uiState.isLoadingBottomSheet,
            isFund = false,
            onDismiss = { onAction(PortfolioIntent.DismissEtfDialog) },
            onAddFund = { isin, quantity ->
                onAction(PortfolioIntent.AddEtfItem(isin, quantity))
            },
            onIsinError = { isinError ->
                onAction(PortfolioIntent.SetError(isinError ?: defaultEtfError))
            }
        )

        // Add Type Detail Bottom Sheet
        uiState.typeDetail?.let { type ->
            InvestmentTypeBottomSheet(
                visible = true,
                type = type,
                investments = uiState.portfolioItems.filter { it.type == type }.toImmutableList(),
                currency = uiState.selectedCurrency,
                onDismiss = { onAction(PortfolioIntent.DismissInvestmentType) },
                isBalanceVisible = isBalanceVisible
            )
        }

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
                portfolioItems = persistentListOf(
                    InvestmentView(
                        symbol = "AAPL",
                        name = "Apple",
                        quantity = 10.0,
                        type = InvestmentType.STOCK,
                        originalCurrency = CurrencyView.get(USD),
                        originalPrice = 150.0,
                        originalPreviousPrice = 140.0,
                        displayPrice = 150.0,
                        displayPreviousPrice = 140.0,
                        changePercent = 0.0,
                        month = 0,
                        year = 0
                    )
                ),
                symbolsWithPrice = persistentListOf("AAPL"),
                totalBalance = 10.0,
                previousBalance = 9.0,
                editingItem = null,
                isFundDialogVisible = false,
                isBankDialogVisible = false,
                isLoading = false,
                themeMode = ThemeMode.LIGHT,
                error = null
            ),
            initialTab = PortfolioTabs.POSITIONS,
            onAction = {}
        )
    }
}
