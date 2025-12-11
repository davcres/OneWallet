package com.davidcrespo.onewallet.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.presentation.contract.PriceIntent
import com.davidcrespo.onewallet.presentation.screens.components.ExpandableFab
import com.davidcrespo.onewallet.presentation.screens.components.PortfolioList
import com.davidcrespo.onewallet.presentation.screens.components.dialogs.BankDepositDialog
import com.davidcrespo.onewallet.presentation.screens.components.dialogs.StockDetailDialog
import com.davidcrespo.onewallet.presentation.viewmodels.PriceViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PriceScreen(
    modifier: Modifier = Modifier,
    viewModel: PriceViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(PriceIntent.LoadInitialData)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with Total Balance
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Balance Total",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "$${String.format("%.2f", uiState.totalBalance)}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Text(
                text = "Tu Portafolio",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            PortfolioList(
                items = uiState.portfolioItems,
                onMove = { from, to -> viewModel.handleIntent(PriceIntent.MoveSymbol(from, to)) },
                onRemove = { viewModel.handleIntent(PriceIntent.RemoveItem(it)) },
                onEdit = { viewModel.handleIntent(PriceIntent.EditQuantity(it)) }
            )
        }

        // Floating Action Button
        ExpandableFab(
            onAddInvestmentClick = { /* TODO: Show search bar */ },
            onAddBankClick = { viewModel.handleIntent(PriceIntent.ShowBankDialog) },
            modifier = Modifier.align(Alignment.BottomEnd)
        )

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

        // Edit Quantity Dialog
        if (uiState.editingItem != null) {
            StockDetailDialog(
                item = uiState.editingItem!!,
                onDismiss = { viewModel.handleIntent(PriceIntent.EditQuantity(null)) },
                onConfirmQuantity = { quantity ->
                    viewModel.handleIntent(PriceIntent.UpdateQuantity(uiState.editingItem!!, quantity))
                }
            )
        }

        // Add Bank/Deposit Dialog
        if (uiState.isBankDialogVisible) {
            BankDepositDialog(
                onDismiss = { viewModel.handleIntent(PriceIntent.DismissBankDialog) },
                onConfirm = { name, amount ->
                    viewModel.handleIntent(PriceIntent.AddBankItem(name, amount))
                }
            )
        }
    }
}