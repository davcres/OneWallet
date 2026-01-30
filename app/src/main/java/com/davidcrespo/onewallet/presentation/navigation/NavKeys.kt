package com.davidcrespo.onewallet.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Route : NavKey {
    @Serializable
    data object Portfolio : Route

    @Serializable
    data class Market(val isCrypto: Boolean) : Route

    @Serializable
    data class Historical(val isBalanceVisible: Boolean) : Route
}
