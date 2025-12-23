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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.presentation.portfolio.components.ExpandableFab
import com.davidcrespo.onewallet.presentation.portfolio.components.PortfolioList
import com.davidcrespo.onewallet.presentation.portfolio.components.TotalBalance
import com.davidcrespo.onewallet.presentation.portfolio.components.dialogs.BankDepositDialog
import com.davidcrespo.onewallet.presentation.portfolio.components.dialogs.FundDepositDialog
import com.davidcrespo.onewallet.presentation.portfolio.components.dialogs.StockDetailDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun PortfolioScreen(
    navigateToHistorical: () -> Unit,
    navigateToMarket: (isCrypto: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PortfolioViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.portfolioItems) {
        viewModel.handleIntent(PortfolioIntent.UpdateBalance)
    }

    var fabButtonExpanded by remember { mutableStateOf(false) }

    val blurRadius by animateDpAsState(
        targetValue = if (fabButtonExpanded) 16.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 1000
        ),
        label = "blur"
    )

    val overlayAlpha by animateFloatAsState(
        targetValue = if (fabButtonExpanded) 0.2f else 0f,
        label = "overlay"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(blurRadius)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TotalBalance(
                    totalBalance = uiState.totalBalance,
                    navigateToHistorical = navigateToHistorical,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Tu Portafolio",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.align(Alignment.Start)
                )

                PortfolioList(
                    items = uiState.portfolioItems,
                    //onMove = { from, to -> viewModel.handleIntent(PortfolioIntent.MoveSymbol(from, to)) },
                    onMove = { from, to -> },
                    onRemove = { viewModel.handleIntent(PortfolioIntent.RemoveItem(it)) },
                    onEdit = { viewModel.handleIntent(PortfolioIntent.EditQuantity(it)) }
                )
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(enabled = false) {}, // Block clicks
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        if (fabButtonExpanded) {
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

        // Floating Action Button
        ExpandableFab(
            expanded = fabButtonExpanded,
            onExpandedChange = { fabButtonExpanded = it },
            onAddStockClick = { navigateToMarket(false) },
            onAddBankClick = { viewModel.handleIntent(PortfolioIntent.ShowBankDialog) },
            onAddFundClick = { viewModel.handleIntent(PortfolioIntent.ShowFundDialog) },
            onAddCryptoClick = { navigateToMarket(true) },
            modifier = Modifier.align(Alignment.BottomEnd)
        )

        // Edit Quantity Dialog
        uiState.editingItem?.let { item ->
            StockDetailDialog(
                item = item,
                onDismiss = { viewModel.handleIntent(PortfolioIntent.EditQuantity(null)) },
                onConfirmQuantity = { quantity ->
                    viewModel.handleIntent(PortfolioIntent.UpdateQuantity(item, quantity))
                }
            )
        }

        // Add Bank/Deposit Dialog
        if (uiState.isBankDialogVisible) {
            BankDepositDialog(
                onDismiss = { viewModel.handleIntent(PortfolioIntent.DismissBankDialog) },
                onConfirm = { name, amount ->
                    viewModel.handleIntent(PortfolioIntent.AddBankItem(name, amount))
                }
            )
        }

        // Add Fund/ETF Dialog
        if (uiState.isFundDialogVisible) {
            FundDepositDialog(
                onDismiss = { viewModel.handleIntent(PortfolioIntent.DismissFundDialog) },
                onConfirm = { name, quantity, price ->
                    viewModel.handleIntent(PortfolioIntent.AddFundItem(name, quantity, price))
                }
            )
        }
    }
}
