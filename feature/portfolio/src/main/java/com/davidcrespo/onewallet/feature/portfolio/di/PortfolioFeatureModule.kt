package com.davidcrespo.onewallet.feature.portfolio.di

import com.davidcrespo.onewallet.feature.portfolio.PortfolioViewModel
import com.davidcrespo.onewallet.feature.portfolio.history.HistoryViewModel
import com.davidcrespo.onewallet.feature.portfolio.worker.PriceAlertWorker
import org.koin.core.module.dsl.viewModelOf
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val portfolioFeatureModule = module {
    viewModelOf(::PortfolioViewModel)
    viewModelOf(::HistoryViewModel)
    worker { PriceAlertWorker(get(), get()) }
}
