package com.davidcrespo.onewallet.feature.onboarding.models

import org.jetbrains.compose.resources.DrawableResource

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: DrawableResource
)
