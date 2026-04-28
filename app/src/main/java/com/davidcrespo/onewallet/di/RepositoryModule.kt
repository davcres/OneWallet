package com.davidcrespo.onewallet.di

import com.davidcrespo.onewallet.data.repository.FinancialRepositoryImpl
import com.davidcrespo.onewallet.data.repository.OnboardingRepositoryImpl
import com.davidcrespo.onewallet.data.repository.PortfolioRepositoryImpl
import com.davidcrespo.onewallet.data.repository.PriceAlertNotificationRepositoryImpl
import com.davidcrespo.onewallet.data.repository.ThemeRepositoryImpl
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import com.davidcrespo.onewallet.domain.repository.OnboardingRepository
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository
import com.davidcrespo.onewallet.domain.repository.PriceAlertNotificationRepository
import com.davidcrespo.onewallet.domain.repository.ThemeRepository
import com.davidcrespo.onewallet.data.repository.FileRepositoryImpl
import com.davidcrespo.onewallet.domain.repository.FileRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<FinancialRepository> {
        FinancialRepositoryImpl(
            get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()
        )
    }
    single<PortfolioRepository> { PortfolioRepositoryImpl(get(), get()) }
    single<OnboardingRepository> { OnboardingRepositoryImpl(get()) }
    single<ThemeRepository> { ThemeRepositoryImpl(get()) }
    single<PriceAlertNotificationRepository> { PriceAlertNotificationRepositoryImpl(get()) }
    single<FileRepository> { FileRepositoryImpl(androidContext(), get()) }
}
