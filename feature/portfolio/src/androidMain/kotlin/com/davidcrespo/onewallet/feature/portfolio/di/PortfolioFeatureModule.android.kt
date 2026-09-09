package com.davidcrespo.onewallet.feature.portfolio.di

import com.davidcrespo.onewallet.feature.portfolio.sync.AndroidWidgetSyncManager
import com.davidcrespo.onewallet.feature.portfolio.sync.WidgetSyncManager
import com.davidcrespo.onewallet.feature.portfolio.worker.PriceAlertWorker
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.Module
import org.koin.dsl.module

actual val portfolioPlatformModule: Module = module {
    single<WidgetSyncManager> { AndroidWidgetSyncManager(get()) }
    worker { PriceAlertWorker(get(), get()) }
}
