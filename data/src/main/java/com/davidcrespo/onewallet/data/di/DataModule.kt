package com.davidcrespo.onewallet.data.di

import org.koin.dsl.module

val dataModule = module {
    includes(
        databaseModule,
        networkModule,
        dataSourceModule,
        cacheModule,
        repositoryModule,
        policyModule
    )
}
