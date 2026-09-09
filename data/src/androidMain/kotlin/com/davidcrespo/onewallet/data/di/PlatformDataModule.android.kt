package com.davidcrespo.onewallet.data.di

import android.content.Context
import android.os.Build
import android.provider.Settings as AndroidSettings
import com.davidcrespo.onewallet.data.BuildConfig
import com.davidcrespo.onewallet.data.local.database.AppDatabase
import com.davidcrespo.onewallet.data.local.database.buildRoomDatabase
import com.davidcrespo.onewallet.data.local.database.getDatabaseBuilder
import com.davidcrespo.onewallet.data.remote.DeviceIdProvider
import com.davidcrespo.onewallet.data.repository.AndroidFileRepository
import com.davidcrespo.onewallet.domain.repository.FileRepository
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformDataModule: org.koin.core.module.Module = module {
    single<AppDatabase> {
        val builder = getDatabaseBuilder(androidContext())
        buildRoomDatabase(builder)
    }

    single<Settings> {
        val prefs = androidContext().getSharedPreferences("onewallet_prefs", Context.MODE_PRIVATE)
        SharedPreferencesSettings(prefs)
    }

    single<DeviceIdProvider> {
        DeviceIdProvider {
            AndroidSettings.Secure.getString(
                androidContext().contentResolver,
                AndroidSettings.Secure.ANDROID_ID
            ) ?: "android-device"
        }
    }

    single<FileRepository> {
        AndroidFileRepository(androidContext(), get())
    }

    single(IS_DEBUG) { BuildConfig.DEBUG }
    single(DEVICE_NAME) { Build.DEVICE }
    single(TELEGRAM_API_KEY) { BuildConfig.TELEGRAM_API_KEY }
    single(TELEGRAM_CHAT_ID) { BuildConfig.TELEGRAM_CHAT_ID }
    single(FINNHUB_KEY) { BuildConfig.FINNHUB_API_KEY }
    single(ALPHA_VANTAGE_KEY) { BuildConfig.ALPHA_VANTAGE_API_KEY }
    single(ALPHA_VANTAGE_KEY_2) { BuildConfig.ALPHA_VANTAGE_API_KEY_2 }
    single(ALPHA_VANTAGE_KEY_3) { BuildConfig.ALPHA_VANTAGE_API_KEY_3 }
    single(MARKETSTACK_KEY) { BuildConfig.MARKETSTACK_API_KEY }
    single(MARKETSTACK_KEY_2) { BuildConfig.MARKETSTACK_API_KEY_2 }
    single(TWELVE_DATA_KEY) { BuildConfig.TWELVE_DATA_API_KEY }
}
