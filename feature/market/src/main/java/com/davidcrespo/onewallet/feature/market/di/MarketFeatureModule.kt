package com.davidcrespo.onewallet.feature.market.di

import com.davidcrespo.onewallet.feature.market.globalMarket.GlobalMarketViewModel
import com.davidcrespo.onewallet.feature.market.usMarket.UsMarketViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val marketFeatureModule = module {
    viewModelOf(::UsMarketViewModel)
    viewModelOf(::GlobalMarketViewModel)
}
