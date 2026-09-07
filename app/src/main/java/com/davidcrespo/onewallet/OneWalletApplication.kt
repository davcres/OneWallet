package com.davidcrespo.onewallet

import android.app.Application
import com.davidcrespo.onewallet.di.appModules
import com.davidcrespo.onewallet.feature.portfolio.worker.PriceAlertWorker
import com.davidcrespo.onewallet.feature.widget.WidgetsRefreshWorker
import com.davidcrespo.onewallet.splash.SplashViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::SplashViewModel)
}

class OneWalletApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@OneWalletApplication)
            workManagerFactory()
            modules(appModules + appModule)
        }

        WidgetsRefreshWorker.enqueuePeriodic(this)
        PriceAlertWorker.enqueuePeriodic(this)
    }
}
