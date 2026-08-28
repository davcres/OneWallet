package com.davidcrespo.onewallet.data.di

import androidx.room.Room
import com.davidcrespo.onewallet.data.local.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "onewallet-db"
        ).build()
    }

    single { get<AppDatabase>().portfolioDao() }
    single { get<AppDatabase>().stockMarketDao() }
    single { get<AppDatabase>().cryptoMarketDao() }
}
