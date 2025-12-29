package com.davidcrespo.onewallet.presentation.historical

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.presentation.historical.composables.HistoricalDetailBottomSheet
import com.davidcrespo.onewallet.presentation.historical.composables.HistoricalList
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricalScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoricalViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(HistoricalIntent.LoadInitialData)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial Mensual") },
                navigationIcon = {
                    IconButton(onClick = {
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
            HistoricalList(
                items = uiState.history,
                onClick = { viewModel.handleIntent(HistoricalIntent.SelectMonth(it.first().year, it.first().month)) },
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            )
        }
    }

    HistoricalDetailBottomSheet(
        investments = uiState.selectedMonthDetail.orEmpty(),
        visible = uiState.selectedMonthDetail != null,
        onDismiss = { viewModel.handleIntent(HistoricalIntent.DismissDetail) }
    )

    if (false && uiState.selectedMonthDetail != null) {
        Dialog(onDismissRequest = { viewModel.handleIntent(HistoricalIntent.DismissDetail) }) {
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
                            if (item.type == InvestmentType.CASH) {
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
                        onClick = { viewModel.handleIntent(HistoricalIntent.DismissDetail) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}
