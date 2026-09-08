package com.davidcrespo.onewallet.feature.portfolio.di

import com.davidcrespo.onewallet.feature.portfolio.PortfolioViewModel
import com.davidcrespo.onewallet.feature.portfolio.history.HistoryViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val portfolioFeatureModule = module {
    includes(portfolioPlatformModule)
    viewModelOf(::PortfolioViewModel)
    viewModelOf(::HistoryViewModel)
}

expect val portfolioPlatformModule: Module
