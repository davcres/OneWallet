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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidcrespo.onewallet.presentation.contract.PriceIntent
import com.davidcrespo.onewallet.presentation.contract.PriceUiState
import com.davidcrespo.onewallet.presentation.designsystem.composables.AnimatedText
import com.davidcrespo.onewallet.presentation.screens.components.ExpandableFab
import com.davidcrespo.onewallet.presentation.screens.components.PortfolioList
import com.davidcrespo.onewallet.presentation.screens.components.dialogs.BankDepositDialog
import com.davidcrespo.onewallet.presentation.screens.components.dialogs.StockDetailDialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@Composable
fun PortfolioScreen(
    uiState: PriceUiState,
    onIntent: (PriceIntent) -> Unit,
    modifier: Modifier = Modifier
) {
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
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = { onIntent(PriceIntent.NavigateToHistory) },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Historial",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Balance Total",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        AnimatedText(
                            text = "${String.format("%.2f", uiState.totalBalance)} €",
                            fontSize = 45.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Text(
                text = "Tu Portafolio",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            PortfolioList(
                items = uiState.portfolioItems,
                onMove = { from, to -> onIntent(PriceIntent.MoveSymbol(from, to)) },
                onRemove = { onIntent(PriceIntent.RemoveItem(it)) },
                onEdit = { onIntent(PriceIntent.EditQuantity(it)) }
            )
        }

        // Floating Action Button
        ExpandableFab(
            onAddInvestmentClick = { onIntent(PriceIntent.NavigateToAddInvestment) },
            onAddBankClick = { onIntent(PriceIntent.ShowBankDialog) },
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
                item = uiState.editingItem,
                onDismiss = { onIntent(PriceIntent.EditQuantity(null)) },
                onConfirmQuantity = { quantity ->
                    onIntent(PriceIntent.UpdateQuantity(uiState.editingItem, quantity))
                }
            )
        }

        // Add Bank/Deposit Dialog
        if (uiState.isBankDialogVisible) {
            BankDepositDialog(
                onDismiss = { onIntent(PriceIntent.DismissBankDialog) },
                onConfirm = { name, amount ->
                    onIntent(PriceIntent.AddBankItem(name, amount))
                }
            )
        }
    }
}
