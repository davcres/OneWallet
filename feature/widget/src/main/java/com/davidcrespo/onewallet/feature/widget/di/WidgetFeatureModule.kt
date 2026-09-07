package com.davidcrespo.onewallet.feature.widget.di

import com.davidcrespo.onewallet.feature.widget.WidgetsRefreshWorker
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val widgetFeatureModule = module {
    worker { WidgetsRefreshWorker(get(), get()) }
}
