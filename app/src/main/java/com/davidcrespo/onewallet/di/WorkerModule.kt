package com.davidcrespo.onewallet.di

import com.davidcrespo.onewallet.presentation.portfolio.worker.PriceAlertWorker
import com.davidcrespo.onewallet.presentation.widget.WidgetsRefreshWorker
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val workerModule = module {
    worker { WidgetsRefreshWorker(get(), get()) }
    worker { PriceAlertWorker(get(), get()) }
}
