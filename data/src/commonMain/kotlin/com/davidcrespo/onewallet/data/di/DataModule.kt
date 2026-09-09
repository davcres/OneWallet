package com.davidcrespo.onewallet.data.di

import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformDataModule: Module

val dataModule = module {
    includes(
        platformDataModule,
        databaseModule,
        networkModule,
        dataSourceModule,
        cacheModule,
        repositoryModule,
        policyModule
    )
}
