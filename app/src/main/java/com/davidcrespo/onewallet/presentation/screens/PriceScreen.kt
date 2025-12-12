package com.davidcrespo.onewallet.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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

    AnimatedContent(
        targetState = uiState.currentScreen,
        transitionSpec = {
            if (targetState == PriceScreenType.Portfolio) {
                // Going back to Portfolio: Slide from Left
                slideInHorizontally(animationSpec = tween(300)) { -it } + fadeIn() togetherWith
                        slideOutHorizontally(animationSpec = tween(300)) { it } + fadeOut()
            } else {
                // Going to Detail/History: Slide from Right
                slideInHorizontally(animationSpec = tween(300)) { it } + fadeIn() togetherWith
                        slideOutHorizontally(animationSpec = tween(300)) { -it } + fadeOut()
            }
        },
        label = "ScreenTransition"
    ) { targetScreen ->
        when (targetScreen) {
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
}