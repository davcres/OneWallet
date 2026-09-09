package com.davidcrespo.onewallet.data.di

import com.davidcrespo.onewallet.data.local.database.AppDatabase
import com.davidcrespo.onewallet.data.local.database.buildRoomDatabase
import com.davidcrespo.onewallet.data.local.database.getDatabaseBuilder
import com.davidcrespo.onewallet.data.remote.DeviceIdProvider
import com.davidcrespo.onewallet.data.repository.IosFileRepository
import com.davidcrespo.onewallet.domain.repository.FileRepository
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIDevice

actual val platformDataModule: org.koin.core.module.Module = module {
    single<AppDatabase> {
        val builder = getDatabaseBuilder()
        buildRoomDatabase(builder)
    }

    single<Settings> {
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
    }

    single<DeviceIdProvider> {
        DeviceIdProvider {
            UIDevice.currentDevice.identifierForVendor?.UUIDString ?: "ios-device"
        }
    }

    single<FileRepository> {
        IosFileRepository(get())
    }

    single(IS_DEBUG) { false }
    single(DEVICE_NAME) { "iOS" }
    single(TELEGRAM_API_KEY) { "" }
    single(TELEGRAM_CHAT_ID) { "" }
    single(FINNHUB_KEY) { "" }
    single(ALPHA_VANTAGE_KEY) { "" }
    single(ALPHA_VANTAGE_KEY_2) { "" }
    single(ALPHA_VANTAGE_KEY_3) { "" }
    single(MARKETSTACK_KEY) { "" }
    single(MARKETSTACK_KEY_2) { "" }
    single(TWELVE_DATA_KEY) { "" }
}
