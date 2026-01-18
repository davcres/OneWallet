package com.davidcrespo.onewallet.di

import com.davidcrespo.onewallet.data.local.cache.CurrencyCache
import com.davidcrespo.onewallet.data.local.cache.CurrencyCacheImpl
import com.davidcrespo.onewallet.data.local.cache.MarketCache
import com.davidcrespo.onewallet.data.local.cache.MarketCacheImpl
import com.davidcrespo.onewallet.data.local.cache.SymbolCache
import com.davidcrespo.onewallet.data.local.cache.SymbolCacheImpl
import org.koin.dsl.module

val cacheModule = module {
    single<SymbolCache> { SymbolCacheImpl(get(), get()) }
    single<CurrencyCache> { CurrencyCacheImpl(get(), get()) }
    single<MarketCache> { MarketCacheImpl(get(), get(), get(), get()) }
}
