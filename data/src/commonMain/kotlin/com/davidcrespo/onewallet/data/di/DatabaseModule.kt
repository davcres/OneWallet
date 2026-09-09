package com.davidcrespo.onewallet.data.di

import com.davidcrespo.onewallet.data.local.database.AppDatabase
import org.koin.dsl.module

val databaseModule = module {
    single { get<AppDatabase>().portfolioDao() }
    single { get<AppDatabase>().stockMarketDao() }
    single { get<AppDatabase>().cryptoMarketDao() }
}
