package com.davidcrespo.onewallet.di

import com.davidcrespo.onewallet.core.di.coreModule
import com.davidcrespo.onewallet.data.di.dataModule
import com.davidcrespo.onewallet.domain.di.domainModule
import com.davidcrespo.onewallet.feature.market.di.marketFeatureModule
import com.davidcrespo.onewallet.feature.onboarding.di.onboardingFeatureModule
import com.davidcrespo.onewallet.feature.portfolio.di.portfolioFeatureModule
import com.davidcrespo.onewallet.feature.widget.di.widgetFeatureModule

val appModules = listOf(
    coreModule,
    domainModule,
    dataModule,
    portfolioFeatureModule,
    marketFeatureModule,
    onboardingFeatureModule,
    widgetFeatureModule
)
