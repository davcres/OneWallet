package com.davidcrespo.onewallet.presentation.splash

import androidx.compose.runtime.Immutable

@Immutable
data class SplashUiState(
    val onboardingCompleted: Boolean? = null
)

sealed interface SplashIntent {
    data object IsOnboardingCompleted : SplashIntent
}
