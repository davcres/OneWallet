package com.davidcrespo.onewallet.presentation.portfolio

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidcrespo.onewallet.presentation.designsystem.composables.AnimatedCounter
import com.davidcrespo.onewallet.presentation.portfolio.components.ExpandableFab
import com.davidcrespo.onewallet.presentation.portfolio.components.PortfolioList
import com.davidcrespo.onewallet.presentation.portfolio.components.dialogs.BankDepositDialog
import com.davidcrespo.onewallet.presentation.portfolio.components.dialogs.FundDepositDialog
import com.davidcrespo.onewallet.presentation.portfolio.components.dialogs.StockDetailDialog
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun PortfolioScreen(
    navigateToHistorical: () -> Unit,
    navigateToMarket: (isCrypto: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PortfolioViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(PortfolioIntent.LoadInitialData)
    }

    LaunchedEffect(uiState.portfolioItems) {
        Log.e("***", "uiState.portfolioItems: ${uiState.portfolioItems}")
        viewModel.handleIntent(PortfolioIntent.UpdateBalance)
    }

    val richPhrases = remember {
        listOf(
            "Demasiado dinero para mostrarlo sin gafas de sol.",
            "A Hacienda le gusta esto.",
            "Eres rico, ¿para qué quieres saber si has ganado 5€ más o menos?",
            "Con esto te dejan entrar al Época sin hacer cola.",
            "Seguro que te puedes permitir hacerle un bizum al humilde desarrollador de la app.",
            "¿Seguro que no has añadido ceros de más? Te dejo un momento para reflexionar.",
            "Deja algo para los demás Javito.",
        )
    }
    var currentRichPhrase by remember { mutableStateOf(richPhrases.random()) }

    LaunchedEffect(uiState.totalBalance) {
        if (uiState.totalBalance > 1_000_000) {
            while (true) {
                currentRichPhrase = richPhrases.random()
                delay(10000)
            }
        }
    }

    BackHandler {
        if (uiState.editingItem != null) {
            viewModel.handleIntent(PortfolioIntent.EditQuantity(null))
        } else if (uiState.isFundDialogVisible) {
            viewModel.handleIntent(PortfolioIntent.DismissBankDialog)
        } else if (uiState.isBankDialogVisible) {
            viewModel.handleIntent(PortfolioIntent.DismissBankDialog)
        }
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
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = navigateToHistorical,
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
                        
                        if (uiState.totalBalance > 1_000_000) {
                            AnimatedContent(
                                targetState = currentRichPhrase,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "RichPhraseTransition"
                            ) { phrase ->
                                Text(
                                    text = phrase,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 30.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        } else {
                            AnimatedCounter(
                                targetValue = uiState.totalBalance,
                                suffix = " €",
                                fontSize = 45.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
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
                //onMove = { from, to -> viewModel.handleIntent(PortfolioIntent.MoveSymbol(from, to)) },
                onMove = { from, to -> },
                onRemove = { viewModel.handleIntent(PortfolioIntent.RemoveItem(it)) },
                onEdit = { viewModel.handleIntent(PortfolioIntent.EditQuantity(it)) }
            )
        }

        // Floating Action Button
        ExpandableFab(
            onAddStockClick = { navigateToMarket(false) },
            onAddBankClick = { viewModel.handleIntent(PortfolioIntent.ShowBankDialog) },
            onAddFundClick = { viewModel.handleIntent(PortfolioIntent.ShowFundDialog) },
            onAddCryptoClick = { navigateToMarket(true) },
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
