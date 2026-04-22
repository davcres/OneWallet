package com.davidcrespo.onewallet.di

import com.davidcrespo.onewallet.MainViewModel
import com.davidcrespo.onewallet.presentation.history.HistoryViewModel
import com.davidcrespo.onewallet.presentation.market.globalMarket.GlobalMarketViewModel
import com.davidcrespo.onewallet.presentation.market.usMarket.UsMarketViewModel
import com.davidcrespo.onewallet.presentation.onboarding.OnboardingViewModel
import com.davidcrespo.onewallet.presentation.onboarding.PortfolioOnboardingViewModel
import com.davidcrespo.onewallet.presentation.portfolio.CurrencyConverter
import com.davidcrespo.onewallet.presentation.portfolio.PortfolioViewModel
import com.davidcrespo.onewallet.presentation.splash.SplashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    single { CurrencyConverter() }

    viewModelOf(::MainViewModel)
    viewModelOf(::SplashViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::PortfolioOnboardingViewModel)
    viewModelOf(::PortfolioViewModel)
    viewModelOf(::UsMarketViewModel)
    viewModelOf(::GlobalMarketViewModel)
    viewModelOf(::HistoryViewModel)
}
