package com.davidcrespo.onewallet.feature.onboarding

sealed interface OnboardingIntent {
    data object SetOnboardingCompleted : OnboardingIntent
}
