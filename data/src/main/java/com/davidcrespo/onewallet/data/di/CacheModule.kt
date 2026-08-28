package com.davidcrespo.onewallet.data.di

import com.davidcrespo.onewallet.data.local.cache.CurrencyCache
import com.davidcrespo.onewallet.data.local.cache.CurrencyCacheImpl
import com.davidcrespo.onewallet.data.local.cache.LocalSymbolCache
import com.davidcrespo.onewallet.data.local.cache.MarketCache
import com.davidcrespo.onewallet.data.local.cache.MarketCacheImpl
import com.davidcrespo.onewallet.data.local.cache.SymbolCacheImpl
import com.davidcrespo.onewallet.domain.cache.SymbolCache
import org.koin.dsl.module
import java.time.Clock

val cacheModule = module {
    single { Clock.systemUTC() }

    single { SymbolCacheImpl(get(), get()) }
    single<LocalSymbolCache> { get<SymbolCacheImpl>() }
    single<SymbolCache> { get<SymbolCacheImpl>() }
    single<CurrencyCache> { CurrencyCacheImpl(get(), get()) }
    single<MarketCache> { MarketCacheImpl(get(), get(), get(), get()) }
}
