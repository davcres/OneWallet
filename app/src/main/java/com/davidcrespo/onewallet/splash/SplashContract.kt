package com.davidcrespo.onewallet.splash

import androidx.compose.runtime.Immutable

@Immutable
data class SplashUiState(
    val onboardingCompleted: Boolean? = null,
    val portfolioOnboardingCompleted: Boolean? = null
)

sealed interface SplashIntent {
    data object LoadMarkets : SplashIntent
    data object IsOnboardingCompleted : SplashIntent
}
