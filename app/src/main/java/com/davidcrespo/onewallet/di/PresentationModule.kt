package com.davidcrespo.onewallet.di

import com.davidcrespo.onewallet.presentation.historical.HistoricalViewModel
import com.davidcrespo.onewallet.presentation.market.MarketViewModel
import com.davidcrespo.onewallet.presentation.onboarding.OnboardingViewModel
import com.davidcrespo.onewallet.presentation.portfolio.CurrencyConverter
import com.davidcrespo.onewallet.presentation.portfolio.PortfolioViewModel
import com.davidcrespo.onewallet.presentation.splash.SplashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    single { CurrencyConverter() }

    viewModelOf(::SplashViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::PortfolioViewModel)
    viewModelOf(::MarketViewModel)
    viewModelOf(::HistoricalViewModel)
}
