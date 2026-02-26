package com.davidcrespo.onewallet.presentation.onboarding.models

import androidx.annotation.DrawableRes

data class OnboardingPage(
    val title: String,
    val description: String,
    @DrawableRes val icon: Int
)
