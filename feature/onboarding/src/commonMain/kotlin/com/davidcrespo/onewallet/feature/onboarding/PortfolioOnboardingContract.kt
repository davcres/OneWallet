package com.davidcrespo.onewallet.feature.onboarding

sealed interface PortfolioOnboardingIntent {
    data object StartTutorial : PortfolioOnboardingIntent
    data object SkipTutorial : PortfolioOnboardingIntent
}
