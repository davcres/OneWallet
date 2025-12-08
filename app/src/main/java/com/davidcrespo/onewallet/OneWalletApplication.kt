package com.davidcrespo.onewallet

import android.app.Application

class OneWalletApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        /*startKoin {
            androidContext(this@OneWalletApplication)
            modules(CoreDiDependencyInjector.modules)

            analytics() // kotzilla analytics
        }*/
    }
}
