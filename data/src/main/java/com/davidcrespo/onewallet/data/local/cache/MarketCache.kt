package com.davidcrespo.onewallet.data.local.cache

import com.davidcrespo.onewallet.data.local.database.market.entities.CryptoMarketEntity
import com.davidcrespo.onewallet.data.local.database.market.entities.StockMarketEntity

interface MarketCache {

    suspend fun getCachedStockMarketIfValid(validCacheHours: Long): List<StockMarketEntity>
    suspend fun getCachedCryptoMarketIfValid(validCacheHours: Long): List<CryptoMarketEntity>
    suspend fun setCachedStockMarket(market: List<StockMarketEntity>)
    suspend fun setCachedCryptoMarket(market: List<CryptoMarketEntity>)
}
