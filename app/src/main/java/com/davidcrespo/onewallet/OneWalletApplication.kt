package com.davidcrespo.onewallet

import android.app.Application
import com.davidcrespo.onewallet.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class OneWalletApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@OneWalletApplication)
            modules(appModule)
        }
    }
}
