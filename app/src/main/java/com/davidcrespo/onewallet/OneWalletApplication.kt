package com.davidcrespo.onewallet

import android.app.Application
import com.davidcrespo.onewallet.di.appModule
import com.davidcrespo.onewallet.presentation.widget.WidgetsRefreshWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

class OneWalletApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@OneWalletApplication)
            workManagerFactory()
            modules(appModule)
        }

        WidgetsRefreshWorker.enqueuePeriodic(this)
    }
}
