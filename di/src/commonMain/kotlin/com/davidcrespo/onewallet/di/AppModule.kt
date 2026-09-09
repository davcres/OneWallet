package com.davidcrespo.onewallet.di

import com.davidcrespo.onewallet.MainViewModel
import com.davidcrespo.onewallet.core.di.coreModule
import com.davidcrespo.onewallet.data.di.dataModule
import com.davidcrespo.onewallet.domain.di.domainModule
import com.davidcrespo.onewallet.feature.market.di.marketFeatureModule
import com.davidcrespo.onewallet.feature.onboarding.di.onboardingFeatureModule
import com.davidcrespo.onewallet.feature.portfolio.di.portfolioFeatureModule
import com.davidcrespo.onewallet.splash.SplashViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appRootModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::SplashViewModel)
}

val sharedAppModules: List<Module> = listOf(
    coreModule,
    domainModule,
    dataModule,
    portfolioFeatureModule,
    marketFeatureModule,
    onboardingFeatureModule,
    appRootModule
)

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(sharedAppModules)
}
