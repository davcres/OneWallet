package com.davidcrespo.onewallet.data.local.cache

import com.davidcrespo.onewallet.data.local.database.market.dao.CryptoMarketDao
import com.davidcrespo.onewallet.data.local.database.market.dao.StockMarketDao
import com.davidcrespo.onewallet.data.local.database.market.entities.CryptoMarketEntity
import com.davidcrespo.onewallet.data.local.database.market.entities.StockMarketEntity
import com.russhwolf.settings.Settings
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.hours

class MarketCacheImpl(
    private val stockMarketDao: StockMarketDao,
    private val cryptoMarketDao: CryptoMarketDao,
    private val settings: Settings,
    private val clock: Clock = Clock.System
): MarketCache {

    override suspend fun getCachedStockMarketIfValid(validCacheHours: Long): List<StockMarketEntity> {
        if (!isValid(KEY_STOCK_CACHED_AT_MILLIS, validCacheHours)) return emptyList()
        return stockMarketDao.getAll().takeIf { it.isNotEmpty() }.orEmpty()
    }

    override suspend fun getCachedCryptoMarketIfValid(validCacheHours: Long): List<CryptoMarketEntity> {
        if (!isValid(KEY_CRYPTO_CACHED_AT_MILLIS, validCacheHours)) return emptyList()
        return cryptoMarketDao.getAll().takeIf { it.isNotEmpty() }.orEmpty()
    }

    override suspend fun setCachedStockMarket(market: List<StockMarketEntity>) {
        stockMarketDao.replaceAll(market)
        settings.putLong(KEY_STOCK_CACHED_AT_MILLIS, clock.now().toEpochMilliseconds())
    }

    override suspend fun setCachedCryptoMarket(market: List<CryptoMarketEntity>) {
        cryptoMarketDao.replaceAll(market)
        settings.putLong(KEY_CRYPTO_CACHED_AT_MILLIS, clock.now().toEpochMilliseconds())
    }

    private fun isValid(timestampKey: String, validCacheHours: Long): Boolean {
        val nowMillis = clock.now().toEpochMilliseconds()
        val cachedAt = settings.getLong(timestampKey, 0L)
        val cacheDurationMillis = validCacheHours.hours.inWholeMilliseconds
        val age = nowMillis - cachedAt
        return cachedAt > 0L && age in 0 until cacheDurationMillis
    }

    companion object {
        private const val KEY_STOCK_CACHED_AT_MILLIS = "stock_cached_at_millis"
        private const val KEY_CRYPTO_CACHED_AT_MILLIS = "crypto_cached_at_millis"
    }
}