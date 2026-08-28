package com.davidcrespo.onewallet.data.local.cache

import android.content.SharedPreferences
import androidx.core.content.edit
import com.davidcrespo.onewallet.data.local.database.market.dao.CryptoMarketDao
import com.davidcrespo.onewallet.data.local.database.market.dao.StockMarketDao
import com.davidcrespo.onewallet.data.local.database.market.entities.CryptoMarketEntity
import com.davidcrespo.onewallet.data.local.database.market.entities.StockMarketEntity
import java.time.Clock
import java.util.concurrent.TimeUnit

class MarketCacheImpl(
    private val stockMarketDao: StockMarketDao,
    private val cryptoMarketDao: CryptoMarketDao,
    private val sharedPreferences: SharedPreferences,
    private val clock: Clock
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

        sharedPreferences.edit {
            putLong(KEY_STOCK_CACHED_AT_MILLIS, clock.millis())
        }
    }

    override suspend fun setCachedCryptoMarket(market: List<CryptoMarketEntity>) {
        cryptoMarketDao.replaceAll(market)

        sharedPreferences.edit {
            putLong(KEY_CRYPTO_CACHED_AT_MILLIS, clock.millis())
        }
    }

    private fun isValid(timestampKey: String, validCacheHours: Long): Boolean {
        val nowMillis = clock.millis()
        val cachedAt = sharedPreferences.getLong(timestampKey, 0L)
        val cacheDurationMillis = TimeUnit.HOURS.toMillis(validCacheHours)
        val age = nowMillis - cachedAt
        return cachedAt > 0L && age in 0 until cacheDurationMillis
    }

    companion object {
        private const val KEY_STOCK_CACHED_AT_MILLIS = "stock_cached_at_millis"
        private const val KEY_CRYPTO_CACHED_AT_MILLIS = "crypto_cached_at_millis"
    }
}