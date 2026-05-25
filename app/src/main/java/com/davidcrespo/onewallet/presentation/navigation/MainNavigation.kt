package com.davidcrespo.onewallet.presentation.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.davidcrespo.onewallet.presentation.market.globalMarket.GlobalMarketRoot
import com.davidcrespo.onewallet.presentation.market.usMarket.UsMarketRoot
import com.davidcrespo.onewallet.presentation.onboarding.OnboardingRoot
import com.davidcrespo.onewallet.presentation.onboarding.PortfolioOnboardingRoot
import com.davidcrespo.onewallet.presentation.portfolio.PortfolioRoot
import com.davidcrespo.onewallet.presentation.splash.SplashRoot

@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(Route.Splash)

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator() // para que los viewmodels sobrevivan solo durante el tiempo de vida de la entry a la que están asociados
        ),
        transitionSpec = { // navegación hacia adelante
            ContentTransform(
                slideInHorizontally(initialOffsetX = { it }),
                slideOutHorizontally()
            )
        },
        popTransitionSpec = { // navegación hacia atrás
            ContentTransform(
                slideInHorizontally(),
                slideOutHorizontally(targetOffsetX = { it })
            )
        },
        entryProvider = entryProvider {
            entry<Route.Splash> {
                SplashRoot(
                    onAnimationFinished = { onboardingCompleted, portfolioOnboardingCompleted ->
                        backStack.clear()
                        if (onboardingCompleted) {
                            if (portfolioOnboardingCompleted) {
                                backStack.add(Route.Portfolio())
                            } else {
                                backStack.add(Route.PortfolioOnboarding)
                            }
                        } else {
                            backStack.add(Route.Onboarding)
                        }
                    }
                )
            }

            entry<Route.Onboarding> {
                OnboardingRoot(
                    onFinish = {
                        backStack.clear()
                        backStack.add(Route.PortfolioOnboarding)
                    }
                )
            }

            entry<Route.PortfolioOnboarding> {
                PortfolioOnboardingRoot(
                    onStartTutorial = {
                        backStack.clear()
                        backStack.add(Route.Portfolio())
                    },
                    onSkipTutorial = {
                        backStack.clear()
                        backStack.add(Route.Portfolio())
                    }
                )
            }

            entry<Route.Portfolio> {
                PortfolioRoot(
                    initialTab = it.tab,
                    navigateToMarket = { isCrypto ->
                        backStack.add(Route.UsMarket(isCrypto = isCrypto))
                    }
                )
            }

            entry<Route.UsMarket> {
                UsMarketRoot(
                    isCrypto = it.isCrypto,
                    navigateToGlobalMarket = {
                        backStack.add(Route.GlobalMarket)
                    },
                    onBack = {
                        if (backStack.size > 1) backStack.removeLastOrNull()
                    }
                )
            }

            entry<Route.GlobalMarket> {
                GlobalMarketRoot(
                    onBack = {
                        while (backStack.size > 1 && backStack.last() !is Route.Portfolio) {
                            backStack.removeLastOrNull()
                        }
                    }
                )
            }
        }
    )
}
