package com.davidcrespo.onewallet.shared.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidcrespo.onewallet.feature.market.globalMarket.GlobalMarketRoot
import com.davidcrespo.onewallet.feature.market.usMarket.UsMarketRoot
import com.davidcrespo.onewallet.feature.onboarding.OnboardingRoot
import com.davidcrespo.onewallet.feature.onboarding.PortfolioOnboardingRoot
import com.davidcrespo.onewallet.feature.portfolio.PortfolioRoot
import com.davidcrespo.onewallet.feature.portfolio.models.PortfolioTabs
import com.davidcrespo.onewallet.splash.SplashIntent
import com.davidcrespo.onewallet.splash.SplashViewModel
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

private const val ANIMATION_DURATION = 500
private const val DISPLAY_DURATION = 800L

sealed interface IosRoute {
    data object Splash : IosRoute
    data object Onboarding : IosRoute
    data object PortfolioOnboarding : IosRoute
    data class Portfolio(val tab: PortfolioTabs = PortfolioTabs.POSITIONS) : IosRoute
    data class UsMarket(val isCrypto: Boolean) : IosRoute
    data object GlobalMarket : IosRoute
}

@Composable
fun IosNavigation(
    modifier: Modifier = Modifier
) {
    val backStack = remember { mutableStateListOf<IosRoute>(IosRoute.Splash) }
    val currentRoute = backStack.lastOrNull() ?: IosRoute.Splash

    AnimatedContent(
        targetState = currentRoute,
        transitionSpec = {
            slideInHorizontally(initialOffsetX = { it }) togetherWith slideOutHorizontally()
        },
        label = "IosNavTransition",
        modifier = modifier.fillMaxSize()
    ) { route ->
        when (route) {
            is IosRoute.Splash -> {
                IosSplashScreen(
                    onAnimationFinished = { onboardingCompleted, portfolioOnboardingCompleted ->
                        backStack.clear()
                        if (onboardingCompleted) {
                            if (portfolioOnboardingCompleted) {
                                backStack.add(IosRoute.Portfolio())
                            } else {
                                backStack.add(IosRoute.PortfolioOnboarding)
                            }
                        } else {
                            backStack.add(IosRoute.Onboarding)
                        }
                    }
                )
            }
            is IosRoute.Onboarding -> {
                OnboardingRoot(
                    onFinish = {
                        backStack.clear()
                        backStack.add(IosRoute.PortfolioOnboarding)
                    }
                )
            }
            is IosRoute.PortfolioOnboarding -> {
                PortfolioOnboardingRoot(
                    onStartTutorial = {
                        backStack.clear()
                        backStack.add(IosRoute.Portfolio())
                    },
                    onSkipTutorial = {
                        backStack.clear()
                        backStack.add(IosRoute.Portfolio())
                    }
                )
            }
            is IosRoute.Portfolio -> {
                PortfolioRoot(
                    initialTab = route.tab,
                    navigateToMarket = { isCrypto ->
                        backStack.add(IosRoute.UsMarket(isCrypto = isCrypto))
                    }
                )
            }
            is IosRoute.UsMarket -> {
                UsMarketRoot(
                    isCrypto = route.isCrypto,
                    navigateToGlobalMarket = {
                        backStack.add(IosRoute.GlobalMarket)
                    },
                    onBack = {
                        if (backStack.size > 1) backStack.removeLastOrNull()
                    }
                )
            }
            is IosRoute.GlobalMarket -> {
                GlobalMarketRoot(
                    onBack = {
                        while (backStack.size > 1 && backStack.last() !is IosRoute.Portfolio) {
                            backStack.removeLastOrNull()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun IosSplashScreen(
    onAnimationFinished: (onboardingCompleted: Boolean, portfolioOnboardingCompleted: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.handleIntent(SplashIntent.LoadMarkets)
        viewModel.handleIntent(SplashIntent.IsOnboardingCompleted)
    }

    var startAnimation by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 4f else 1f,
        animationSpec = tween(durationMillis = ANIMATION_DURATION),
        label = "scaleAnimation"
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 1f,
        animationSpec = tween(durationMillis = ANIMATION_DURATION),
        label = "alphaAnimation"
    )

    LaunchedEffect(Unit) {
        delay(DISPLAY_DURATION)
        startAnimation = true
        delay(ANIMATION_DURATION.toLong())
        onAnimationFinished(
            uiState.onboardingCompleted ?: false,
            uiState.portfolioOnboardingCompleted ?: false
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable {
                if (!startAnimation) {
                    startAnimation = true
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.AccountBalanceWallet,
            contentDescription = "OneWallet Logo",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(96.dp)
                .alpha(alpha)
                .scale(scale)
        )
    }
}
