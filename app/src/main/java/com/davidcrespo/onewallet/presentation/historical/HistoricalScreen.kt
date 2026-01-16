package com.davidcrespo.onewallet.presentation.historical

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidcrespo.onewallet.presentation.historical.composables.HistoricalDetailBottomSheet
import com.davidcrespo.onewallet.presentation.historical.composables.HistoricalInvestmentDetail
import com.davidcrespo.onewallet.presentation.historical.composables.HistoricalList
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoricalRoot(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoricalViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HistoricalScreen(
        uiState = uiState,
        onAction = viewModel::handleIntent,
        onBack = onBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoricalScreen(
    uiState: HistoricalUiState,
    onAction: (HistoricalIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onAction(HistoricalIntent.LoadInitialData)
    }

    val blurRadius by animateDpAsState(
        targetValue = if (uiState.selectedMonthDetail != null) 16.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 1000
        ),
        label = "blur"
    )

    val overlayAlpha by animateFloatAsState(
        targetValue = if (uiState.selectedMonthDetail != null) 0.32f else 0f,
        label = "overlay"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .blur(blurRadius)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Historial Mensual") },
                    navigationIcon = {
                        IconButton(onClick = {
                            onBack()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                    currency = uiState.selectedCurrency,
                    onClick = { onAction(HistoricalIntent.SelectMonth(it.first().year, it.first().month)) },
                    modifier = Modifier
                        .padding(padding)
                        .blur(blurRadius)
                )
            }
        }

        if (uiState.selectedMonthDetail != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = overlayAlpha))
            )
        }

        HistoricalDetailBottomSheet(
            investments = uiState.selectedMonthDetail.orEmpty(),
            previousInvestments = uiState.selectedPreviousMonth.orEmpty(),
            currency = uiState.selectedCurrency,
            visible = uiState.selectedMonthDetail != null,
            onClickInvestment = { onAction(HistoricalIntent.SelectInvestment(it)) },
            onDismiss = { onAction(HistoricalIntent.DismissBottomSheet) }
        )

        uiState.selectedInvestment?.let {
            HistoricalInvestmentDetail(
                investment = it,
                previousMonthInvestment = uiState.selectedPreviousInvestment,
                onDismiss = { onAction(HistoricalIntent.DismissInvestmentDetail) }
            )
        }
    }
}
