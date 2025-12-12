package com.davidcrespo.onewallet.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.davidcrespo.onewallet.presentation.contract.PriceIntent
import com.davidcrespo.onewallet.presentation.contract.PriceScreenType
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

    when (uiState.currentScreen) {
        PriceScreenType.Portfolio -> {
            PortfolioScreen(
                uiState = uiState,
                onIntent = viewModel::handleIntent,
                modifier = modifier
            )
        }
        PriceScreenType.AddInvestment -> {
            AddInvestmentScreen(
                uiState = uiState,
                onIntent = viewModel::handleIntent,
                modifier = modifier
            )
        }
        PriceScreenType.History -> {
            HistoryScreen(
                onBack = { viewModel.handleIntent(PriceIntent.NavigateBack) }
            )
        }
    }
}