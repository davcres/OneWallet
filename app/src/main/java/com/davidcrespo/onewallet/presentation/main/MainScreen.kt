package com.davidcrespo.onewallet.presentation.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.davidcrespo.onewallet.presentation.historical.HistoryScreen
import com.davidcrespo.onewallet.presentation.market.AddInvestmentScreen
import com.davidcrespo.onewallet.presentation.portfolio.PortfolioScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AnimatedContent(
        targetState = uiState.screenStack.lastOrNull(),
        transitionSpec = {
            if (targetState == ScreenType.Portfolio) {
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
            ScreenType.Portfolio -> {
                PortfolioScreen(
                    navigateToHistorical = { viewModel.handleIntent(MainIntent.NavigateToHistorical) },
                    navigateToMarket = { isCrypto ->
                        viewModel.handleIntent(MainIntent.NavigateToMarket(isCrypto))
                    },
                    modifier = modifier
                )
            }
            ScreenType.Market -> {
                AddInvestmentScreen(
                    isCrypto = uiState.isCrypto,
                    onBack = { viewModel.handleIntent(MainIntent.OnBack) },
                    modifier = modifier
                )
            }
            ScreenType.Historical -> {
                HistoryScreen(
                    onBack = { viewModel.handleIntent(MainIntent.OnBack) }
                )
            }
            else -> {}
        }
    }
}