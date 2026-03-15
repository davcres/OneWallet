package com.davidcrespo.onewallet.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.davidcrespo.onewallet.presentation.portfolio.models.PortfolioTabs
import kotlinx.serialization.Serializable

sealed interface Route : NavKey {
    @Serializable
    data object Splash : Route

    @Serializable
    data object Onboarding : Route

    @Serializable
    data class Portfolio(val tab: PortfolioTabs = PortfolioTabs.POSITIONS) : Route

    @Serializable
    data class UsMarket(val isCrypto: Boolean) : Route

    @Serializable
    data object GlobalMarket : Route

    @Serializable
    data class Historical(val isBalanceVisible: Boolean) : Route
}
