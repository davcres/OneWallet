package com.davidcrespo.onewallet.feature.onboarding.di

import com.davidcrespo.onewallet.feature.onboarding.OnboardingViewModel
import com.davidcrespo.onewallet.feature.onboarding.PortfolioOnboardingViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val onboardingFeatureModule = module {
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::PortfolioOnboardingViewModel)
}
