package com.davidcrespo.onewallet.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.activity.compose.BackHandler
import com.davidcrespo.onewallet.presentation.contract.HistoryIntent
import com.davidcrespo.onewallet.presentation.viewmodels.HistoryViewModel
import org.koin.androidx.compose.koinViewModel
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    BackHandler {
        if (uiState.selectedMonthDetail != null) {
            viewModel.handleIntent(HistoryIntent.DismissDetail)
        } else {
            viewModel.handleIntent(HistoryIntent.NavigateBack)
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial Mensual") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.handleIntent(HistoryIntent.NavigateBack)
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.history) { balance ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.handleIntent(HistoryIntent.SelectMonth(balance.year, balance.month))
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val monthName = Month.of(balance.month)
                                .getDisplayName(TextStyle.FULL, Locale.getDefault())
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                            
                            Text(
                                text = "$monthName ${balance.year}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "%.2f €".format(balance.totalBalance),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.selectedMonthDetail != null) {
        Dialog(onDismissRequest = { viewModel.handleIntent(HistoryIntent.DismissDetail) }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Detalle del Mes",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 400.dp)
                    ) {
                        items(uiState.selectedMonthDetail.orEmpty()) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                            if (item.price == 1.0 && item.currency == "EUR") {
                                // Do not display quantity x price for cash/bank items
                                Text(
                                    text = item.symbol, // Display only the bank name
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Column {
                                    Text(
                                        text = item.symbol,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${item.quantity} u. x %.2f €".format(item.price),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                                Text(
                                    text = "%.2f €".format(item.quantity * item.price),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.handleIntent(HistoryIntent.DismissDetail) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}
