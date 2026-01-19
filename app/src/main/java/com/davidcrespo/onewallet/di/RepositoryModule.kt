package com.davidcrespo.onewallet.di

import com.davidcrespo.onewallet.data.repository.FinancialRepositoryImpl
import com.davidcrespo.onewallet.data.repository.PortfolioRepositoryImpl
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<FinancialRepository> {
        FinancialRepositoryImpl(
            get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()
        )
    }
    single<PortfolioRepository> { PortfolioRepositoryImpl(get(), get()) }
}
