package com.davidcrespo.onewallet.presentation.onboarding

sealed interface OnboardingIntent {
    data object SetOnboardingCompleted : OnboardingIntent
}
