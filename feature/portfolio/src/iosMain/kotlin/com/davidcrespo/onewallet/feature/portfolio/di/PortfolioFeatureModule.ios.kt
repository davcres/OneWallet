package com.davidcrespo.onewallet.feature.portfolio.di

import com.davidcrespo.onewallet.feature.portfolio.sync.IosWidgetSyncManager
import com.davidcrespo.onewallet.feature.portfolio.sync.WidgetSyncManager
import org.koin.core.module.Module
import org.koin.dsl.module

actual val portfolioPlatformModule: Module = module {
    single<WidgetSyncManager> { IosWidgetSyncManager() }
}
