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
    private val sharedPreferences: SharedPreferences
): MarketCache {

    override suspend fun getCachedStockMarketIfValid(validCacheHours: Long): List<StockMarketEntity> {
        val nowMillis = Clock.systemUTC().millis()

        val cacheDurationMillis = TimeUnit.HOURS.toMillis(validCacheHours)

        val cachedAt = sharedPreferences.getLong(KEY_STOCK_CACHED_AT_MILLIS, 0L)
        val cachedStockMarket = stockMarketDao.getAll()

        val isCacheValid = cachedStockMarket.isNotEmpty() &&
                cachedAt > 0L &&
                (nowMillis - cachedAt) in 0 until cacheDurationMillis

        return if (isCacheValid) {
            cachedStockMarket
        } else {
            emptyList()
        }
    }

    override suspend fun getCachedCryptoMarketIfValid(validCacheHours: Long): List<CryptoMarketEntity> {
        val nowMillis = Clock.systemUTC().millis()

        val cacheDurationMillis = TimeUnit.HOURS.toMillis(validCacheHours)

        val cachedAt = sharedPreferences.getLong(KEY_CRYPTO_CACHED_AT_MILLIS, 0L)
        val cachedCryptoMarket = cryptoMarketDao.getAll()

        val isCacheValid = cachedCryptoMarket.isNotEmpty() &&
                cachedAt > 0L &&
                (nowMillis - cachedAt) in 0 until cacheDurationMillis

        return if (isCacheValid) {
            cachedCryptoMarket
        } else {
            emptyList()
        }
    }

    override suspend fun setCachedStockMarket(market: List<StockMarketEntity>) {
        val nowMillis = Clock.systemUTC().millis()

        stockMarketDao.replaceAll(market)

        sharedPreferences.edit {
            putLong(KEY_STOCK_CACHED_AT_MILLIS, nowMillis)
        }
    }

    override suspend fun setCachedCryptoMarket(market: List<CryptoMarketEntity>) {
        val nowMillis = Clock.systemUTC().millis()

        cryptoMarketDao.replaceAll(market)

        sharedPreferences.edit {
            putLong(KEY_CRYPTO_CACHED_AT_MILLIS, nowMillis)
        }
    }

    companion object {
        private const val KEY_STOCK_CACHED_AT_MILLIS = "stock_cached_at_millis"
        private const val KEY_CRYPTO_CACHED_AT_MILLIS = "crypto_cached_at_millis"
    }
}