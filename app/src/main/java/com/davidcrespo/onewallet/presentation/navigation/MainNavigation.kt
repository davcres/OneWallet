package com.davidcrespo.onewallet.presentation.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.davidcrespo.onewallet.presentation.historical.HistoricalRoot
import com.davidcrespo.onewallet.presentation.market.MarketRoot
import com.davidcrespo.onewallet.presentation.portfolio.PortfolioRoot
import com.davidcrespo.onewallet.presentation.splash.SplashScreen

@Composable
fun MainNavigation(
    contentPadding: PaddingValues
) {
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
                SplashScreen(
                    onAnimationFinished = {
                        backStack.clear()
                        backStack.add(Route.Portfolio)
                    }
                )
            }

            entry<Route.Portfolio> {
                PortfolioRoot(
                    navigateToHistorical = { isBalanceVisible ->
                        backStack.add(Route.Historical(isBalanceVisible = isBalanceVisible))
                    },
                    navigateToMarket = { isCrypto ->
                        backStack.add(Route.Market(isCrypto = isCrypto))
                    },
                    modifier = Modifier.padding(contentPadding)
                )
            }

            entry<Route.Market> {
                MarketRoot(
                    isCrypto = it.isCrypto,
                    onBack = {
                        if (backStack.size > 1) backStack.removeLastOrNull()
                    },
                    modifier = Modifier.padding(contentPadding)
                )
            }

            entry<Route.Historical> {
                HistoricalRoot(
                    isBalanceVisible = it.isBalanceVisible,
                    onBack = {
                        if (backStack.size > 1) backStack.removeLastOrNull()
                    },
                    modifier = Modifier.padding(contentPadding)
                )
            }
        }
    )
}
