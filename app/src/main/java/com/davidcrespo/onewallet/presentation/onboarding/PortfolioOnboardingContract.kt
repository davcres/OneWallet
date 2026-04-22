package com.davidcrespo.onewallet.presentation.onboarding

sealed interface PortfolioOnboardingIntent {
    data object StartTutorial : PortfolioOnboardingIntent
    data object SkipTutorial : PortfolioOnboardingIntent
}
