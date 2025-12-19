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
import com.davidcrespo.onewallet.presentation.historical.HistoricalScreen
import com.davidcrespo.onewallet.presentation.market.MarketScreen
import com.davidcrespo.onewallet.presentation.portfolio.PortfolioScreen

@Composable
fun MainNavigation(
    contentPadding: PaddingValues
) {
    val backStack = rememberNavBackStack(Route.Portfolio)

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
            entry<Route.Portfolio> {
                PortfolioScreen(
                    navigateToHistorical = {
                        backStack.add(Route.Historical)
                    },
                    navigateToMarket = { isCrypto ->
                        backStack.add(Route.Market(isCrypto = isCrypto))
                    },
                    modifier = Modifier.padding(contentPadding)
                )
            }

            entry<Route.Market> {
                MarketScreen(
                    isCrypto = it.isCrypto,
                    onBack = {
                        if (backStack.size > 1) backStack.removeLastOrNull()
                    },
                    modifier = Modifier.padding(contentPadding)
                )
            }

            entry<Route.Historical> {
                HistoricalScreen(
                    onBack = {
                        if (backStack.size > 1) backStack.removeLastOrNull()
                    },
                    modifier = Modifier.padding(contentPadding)
                )
            }
        }
    )
}
