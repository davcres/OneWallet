package com.davidcrespo.onewallet.presentation.portfolio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Celebration
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
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.core.composables.ErrorBanner
import com.davidcrespo.onewallet.core.composables.OWDialog
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
import com.davidcrespo.onewallet.presentation.portfolio.models.PortfolioCoachmarks
import com.davidcrespo.onewallet.presentation.portfolio.models.PortfolioTabs
import com.davidcrespo.onewallet.presentation.portfolio.positions.PositionsTab
import com.davidcrespo.onewallet.presentation.portfolio.prices.PricesTab
import com.pseudoankit.coachmark.LocalCoachMarkScope
import com.pseudoankit.coachmark.UnifyCoachmark
import com.pseudoankit.coachmark.model.HighlightedViewConfig
import com.pseudoankit.coachmark.model.OverlayClickEvent
import com.pseudoankit.coachmark.model.ToolTipPlacement
import com.pseudoankit.coachmark.overlay.DimOverlayEffect
import com.pseudoankit.coachmark.scope.enableCoachMark
import com.pseudoankit.coachmark.shape.Arrow
import com.pseudoankit.coachmark.shape.Balloon
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import kotlin.math.abs

@Composable
fun PortfolioRoot(
    initialTab: PortfolioTabs,
    navigateToMarket: (isCrypto: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PortfolioViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(initialTab) {
        viewModel.handleIntent(PortfolioIntent.SetTab(initialTab))
    }

    var coachMarkScope by remember { mutableStateOf<com.pseudoankit.coachmark.scope.CoachMarkScope?>(null) }

    UnifyCoachmark(
        overlayEffect = DimOverlayEffect(Color.Black.copy(alpha = .5f)),
        onOverlayClicked = {
            val current = uiState.onboardingPlaylist.firstOrNull()
            if (current == PortfolioCoachmarks.EDIT_INVESTMENT) {
                uiState.portfolioItems.firstOrNull()?.let {
                    viewModel.handleIntent(PortfolioIntent.EditQuantity(it))
                    viewModel.handleIntent(PortfolioIntent.NextOnboardingStep)
                    coachMarkScope?.hide()
                }
                OverlayClickEvent.None
            } else if (current == PortfolioCoachmarks.DELETE_INVESTMENT) {
                uiState.portfolioItems.firstOrNull()?.let {
                    viewModel.handleIntent(PortfolioIntent.ShowDeleteDialog(it))
                    viewModel.handleIntent(PortfolioIntent.NextOnboardingStep)
                    coachMarkScope?.hide()
                }
                OverlayClickEvent.None
            } else if (current == PortfolioCoachmarks.ADD_INVESTMENT) {
                viewModel.handleIntent(PortfolioIntent.ShowAddInvestment)
                viewModel.handleIntent(PortfolioIntent.NextOnboardingStep)
                coachMarkScope?.hide()
                OverlayClickEvent.None
            } else if (uiState.editingItem == null && uiState.deletingItem == null && !uiState.isAddInvestmentVisible) {
                viewModel.handleIntent(PortfolioIntent.NextOnboardingStep)
                OverlayClickEvent.GoNext
            } else {
                OverlayClickEvent.None
            }
        }
    ) {
        coachMarkScope = this

        val isAnySheetVisible = uiState.isFundDialogVisible ||
                uiState.isEtfDialogVisible ||
                uiState.isBankDialogVisible ||
                uiState.isOtherDialogVisible ||
                uiState.isAddInvestmentVisible ||
                uiState.editingItem != null ||
                uiState.deletingItem != null ||
                uiState.typeDetail != null

        val isOnboardingActive = uiState.onboardingPlaylist.isNotEmpty()

        Box(modifier = modifier.fillMaxSize()) {
            PortfolioScreen(
                uiState = uiState,
                onAction = { action ->
                    when (action) {
                        is PortfolioIntent.NavigateToMarket -> navigateToMarket(action.isCrypto)
                        else -> viewModel.handleIntent(action)
                    }
                },
                isInteractionEnabled = !isOnboardingActive || isAnySheetVisible
            )

            // Input blocker to prevent user from breaking onboarding during transitions
            if (isOnboardingActive && !isAnySheetVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        )
                )
            }
        }

        // Initial trigger
        LaunchedEffect(uiState.portfolioItems) {
            if (uiState.portfolioItems.isNotEmpty() && uiState.onboardingPlaylist.isEmpty() && !uiState.isOnboardingCompleted) {
                viewModel.handleIntent(PortfolioIntent.StartOnboarding)
            }
        }

        // Trigger onboarding for each tab
        LaunchedEffect(uiState.onboardingPlaylist.firstOrNull()?.tab) {
            if (uiState.onboardingPlaylist.isNotEmpty()) {
                val currentTabBatch = mutableListOf<PortfolioCoachmarks>()
                for (item in uiState.onboardingPlaylist) {
                    if (item.tab == uiState.selectedTab) {
                        currentTabBatch.add(item)
                        if (item == PortfolioCoachmarks.EDIT_INVESTMENT || item == PortfolioCoachmarks.DELETE_INVESTMENT || item == PortfolioCoachmarks.ADD_INVESTMENT) break
                    } else break
                }

                if (currentTabBatch.isNotEmpty()) {
                    delay(600)
                    show(*currentTabBatch.toTypedArray())
                }
            }
        }

        // Resume onboarding after closing edit bottom sheet
        LaunchedEffect(uiState.editingItem) {
            if (uiState.editingItem == null && uiState.onboardingPlaylist.firstOrNull() == PortfolioCoachmarks.DELETE_INVESTMENT) {
                delay(500) // Wait for bottom sheet to close
                show(PortfolioCoachmarks.DELETE_INVESTMENT)
            }
        }

        // Resume onboarding after closing delete bottom sheet
        LaunchedEffect(uiState.deletingItem) {
            if (uiState.deletingItem == null && uiState.onboardingPlaylist.firstOrNull() == PortfolioCoachmarks.ADD_INVESTMENT) {
                delay(500) // Wait for bottom sheet to close
                viewModel.handleIntent(PortfolioIntent.ShowOnboardingCompletionDialog)
            }
        }

        // Show final ADD_INVESTMENT tooltip after clearing portfolio
        LaunchedEffect(uiState.showOnboardingCompletionDialog) {
            if (!uiState.showOnboardingCompletionDialog && uiState.onboardingPlaylist.firstOrNull() == PortfolioCoachmarks.ADD_INVESTMENT && !uiState.isOnboardingCompleted) {
                delay(800)
                show(PortfolioCoachmarks.ADD_INVESTMENT)
            }
        }
    }
}

@Composable
private fun PortfolioScreen(
    uiState: PortfolioUiState,
    onAction: (PortfolioIntent) -> Unit,
    modifier: Modifier = Modifier,
    isInteractionEnabled: Boolean = true
) {
    var isBalanceVisible by rememberSaveable { mutableStateOf(true) }
    val stateHolder = rememberSaveableStateHolder()

    val hideBackground =
        uiState.isFundDialogVisible ||
                uiState.isEtfDialogVisible ||
                uiState.isBankDialogVisible ||
                uiState.isOtherDialogVisible ||
                uiState.isAddInvestmentVisible ||
                uiState.editingItem != null ||
                uiState.deletingItem != null ||
                uiState.typeDetail != null

    val isEditOnboardingActive = uiState.onboardingPlaylist.firstOrNull() == PortfolioCoachmarks.EDIT_INVESTMENT && !hideBackground
    val isDeleteOnboardingActive = uiState.onboardingPlaylist.firstOrNull() == PortfolioCoachmarks.DELETE_INVESTMENT && !hideBackground
    val isAddOnboardingActive = uiState.onboardingPlaylist.firstOrNull() == PortfolioCoachmarks.ADD_INVESTMENT && !hideBackground

    var isFabPressed by remember { mutableStateOf(false) }
    LaunchedEffect(isAddOnboardingActive) {
        if (isAddOnboardingActive) {
            while (true) {
                delay(1000)
                isFabPressed = true
                delay(3000)
                isFabPressed = false
                delay(2000)
            }
        } else {
            isFabPressed = false
        }
    }

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
            onShake = { if (isInteractionEnabled) isBalanceVisible = !isBalanceVisible }
        )
    }

    Scaffold(
        floatingActionButton = {
            OWFloatingActionButton(
                expanded = uiState.isAddInvestmentVisible,
                onExpandedChange = { if (it) onAction(PortfolioIntent.ShowAddInvestment) else onAction(PortfolioIntent.DismissAddInvestment) },
                isPressedForced = isFabPressed,
                modifier = Modifier
                    .applyIf(uiState.portfolioItems.isEmpty()) { pulse() }
                    .enableCoachMark(
                        key = PortfolioCoachmarks.ADD_INVESTMENT,
                        toolTipPlacement = ToolTipPlacement.Start,
                        tooltip = {
                            Balloon(
                                arrow = Arrow.End(),
                                modifier = Modifier.widthIn(max = 200.dp),
                                bgColor = MaterialTheme.colorScheme.primaryContainer,
                                cornerRadius = 16.dp,
                                padding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = stringResource(PortfolioCoachmarks.ADD_INVESTMENT.tooltip),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = TextAlign.Center
                                )
                            }
                        },
                        coachMarkScope = LocalCoachMarkScope.current
                    )
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            AnimatedVisibility(
                uiState.portfolioItems.isNotEmpty(),
                enter = slideInVertically(
                    initialOffsetY = { it }
                ),
                exit = slideOutVertically(
                    animationSpec = tween(200, easing = EaseOut),
                    targetOffsetY = { it }
                )
            ) {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    )
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        PortfolioTabs.entries.forEach { tab ->
                            val selected = uiState.selectedTab == tab
                            val animatedScale by animateFloatAsState(
                                targetValue = if (selected) 1.2f else 1f,
                                animationSpec = tween(300, easing = LinearEasing),
                                label = "nav_item_scale"
                            )

                            val coachmarkKey = when (tab) {
                                PortfolioTabs.POSITIONS -> PortfolioCoachmarks.POSITIONS_TAB
                                PortfolioTabs.ALLOCATION -> PortfolioCoachmarks.ALLOCATION_TAB
                                PortfolioTabs.PRICES -> PortfolioCoachmarks.PRICES_TAB
                                PortfolioTabs.HISTORY -> PortfolioCoachmarks.HISTORY_TAB
                            }

                            NavigationBarItem(
                                selected = selected,
                                onClick = { if (isInteractionEnabled) onAction(PortfolioIntent.SetTab(tab)) },
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
                                ),
                                modifier = Modifier
                                    .enableCoachMark(
                                        key = coachmarkKey,
                                        toolTipPlacement = ToolTipPlacement.Top,
                                        highlightedViewConfig = HighlightedViewConfig(
                                            shape = HighlightedViewConfig.Shape.Rect(100.dp),
                                            padding = PaddingValues(0.dp)
                                        ),
                                        tooltip = {
                                            val hOffset = when (tab) {
                                                PortfolioTabs.POSITIONS -> 56.dp
                                                PortfolioTabs.HISTORY -> (-56).dp
                                                else -> 0.dp
                                            }
                                            // Modifier.layout para que el offset se aplique en la fase de medición
                                            // antes de que se empiece a dibujar el componente
                                            Box(modifier = Modifier.layout { measurable, constraints ->
                                                val placeable = measurable.measure(constraints)
                                                val offsetPx = hOffset.roundToPx()
                                                // We expand the width to encompass the offset,
                                                // ensuring the library sees the full area.
                                                val expandedWidth = placeable.width + abs(offsetPx) * 2
                                                layout(expandedWidth, placeable.height) {
                                                    // Center the original balloon, then apply the offset.
                                                    val x = (expandedWidth - placeable.width) / 2 + offsetPx
                                                    placeable.place(x, 0)
                                                }
                                            }) {
                                                Balloon(
                                                    arrow = when (tab) {
                                                        PortfolioTabs.POSITIONS -> Arrow.Bottom(bias = 0.2f)
                                                        PortfolioTabs.HISTORY -> Arrow.Bottom(bias = 0.8f)
                                                        else -> Arrow.Bottom()
                                                    },
                                                    modifier = Modifier.widthIn(max = 150.dp),
                                                    bgColor = MaterialTheme.colorScheme.primaryContainer,
                                                    cornerRadius = 16.dp,
                                                    padding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                                                ) {
                                                    Text(
                                                        text = stringResource(coachmarkKey.tooltip),
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                            }
                                        },
                                        coachMarkScope = LocalCoachMarkScope.current
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
                    text = stringResource(uiState.selectedTab.description),
                    currency = uiState.selectedCurrency,
                    themeMode = uiState.themeMode,
                    onCurrencyChange = { if (isInteractionEnabled) onAction(PortfolioIntent.ChangeCurrency) },
                    onChangeUIMode = { if (isInteractionEnabled) onAction(PortfolioIntent.ToggleTheme(it)) },
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
                            targetState = uiState.selectedTab,
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
                                        isActivePage = true,
                                        isEditOnboardingActive = isEditOnboardingActive,
                                        isDeleteOnboardingActive = isDeleteOnboardingActive
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
                                        currency = uiState.selectedCurrency,
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

        if (uiState.isAddInvestmentVisible) {
            AddInvestmentBottomSheet(
                visible = uiState.isAddInvestmentVisible,
                onDismiss = { onAction(PortfolioIntent.DismissAddInvestment) },
                onAssetTypeClick = { asset ->
                    when (asset) {
                        InvestmentType.STOCK -> onAction(PortfolioIntent.NavigateToMarket(false))
                        InvestmentType.CRYPTO -> onAction(PortfolioIntent.NavigateToMarket(true))
                        InvestmentType.FUND -> onAction(PortfolioIntent.ShowFundDialog)
                        InvestmentType.ETF -> onAction(PortfolioIntent.ShowEtfDialog)
                        InvestmentType.BANK -> onAction(PortfolioIntent.ShowBankDialog)
                        InvestmentType.OTHER -> onAction(PortfolioIntent.ShowOtherDialog)
                    }
                    onAction(PortfolioIntent.DismissAddInvestment)
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
                onEditInvestment = { quantity, alertThreshold ->
                    onAction(PortfolioIntent.UpdateQuantity(item, quantity, alertThreshold))
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

        if (uiState.showOnboardingCompletionDialog) {
            OWDialog(
                title = stringResource(R.string.onboarding_finished_title),
                description = stringResource(R.string.onboarding_finished_description),
                primaryButtonText = stringResource(R.string.onboarding_finished_button),
                onPrimaryClick = {
                    onAction(PortfolioIntent.ClearPortfolio)
                    onAction(PortfolioIntent.DismissOnboardingCompletionDialog)
                },
                icon = Icons.Outlined.Celebration
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
                error = null,
                selectedTab = PortfolioTabs.POSITIONS
            ),
            onAction = {},
        )
    }
}
