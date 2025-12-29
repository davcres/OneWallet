package com.davidcrespo.onewallet.presentation.historical

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidcrespo.onewallet.presentation.historical.composables.HistoricalDetailBottomSheet
import com.davidcrespo.onewallet.presentation.historical.composables.HistoricalInvestmentDetail
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
        previousInvestments = uiState.selectedPreviousMonth.orEmpty(),
        visible = uiState.selectedMonthDetail != null,
        onClickInvestment = { viewModel.handleIntent(HistoricalIntent.SelectInvestment(it)) },
        onDismiss = { viewModel.handleIntent(HistoricalIntent.DismissBottomSheet) }
    )

    uiState.selectedInvestment?.let {
        HistoricalInvestmentDetail(
            investment = it,
            previousMonthInvestment = uiState.selectedPreviousInvestment,
            onDismiss = { viewModel.handleIntent(HistoricalIntent.DismissInvestmentDetail) }
        )
    }
}
