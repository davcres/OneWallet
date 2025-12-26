package com.davidcrespo.onewallet.data.local.cache

import android.content.SharedPreferences
import androidx.core.content.edit
import com.davidcrespo.onewallet.data.local.database.market.dao.CryptoMarketDao
import com.davidcrespo.onewallet.data.local.database.market.dao.StockMarketDao
import com.davidcrespo.onewallet.data.local.database.market.entities.CryptoMarketEntity
import com.davidcrespo.onewallet.data.local.database.market.entities.StockMarketEntity
import com.davidcrespo.onewallet.data.remote.telegram.TelegramApiClient
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class MarketCacheImpl(
    private val stockMarketDao: StockMarketDao,
    private val cryptoMarketDao: CryptoMarketDao,
    private val sharedPreferences: SharedPreferences,
    private val telegramApiClient: TelegramApiClient
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
            //telegramApiClient.sendMessage("get stock market from cache at ${formatUtcMillis(nowMillis)}")
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
            //telegramApiClient.sendMessage("get crypto market from cache at ${formatUtcMillis(nowMillis)}")
            cachedCryptoMarket
        } else {
            emptyList()
        }
    }

    override suspend fun setCachedStockMarket(market: List<StockMarketEntity>) {
        val nowMillis = Clock.systemUTC().millis()

        telegramApiClient.sendMessage("SET stock market to cache at ${formatUtcMillis(nowMillis)}")

        stockMarketDao.replaceAll(market)

        sharedPreferences.edit {
            putLong(KEY_STOCK_CACHED_AT_MILLIS, nowMillis)
        }
    }

    override suspend fun setCachedCryptoMarket(market: List<CryptoMarketEntity>) {
        val nowMillis = Clock.systemUTC().millis()

        telegramApiClient.sendMessage("SET crypto market to cache at ${formatUtcMillis(nowMillis)}")

        cryptoMarketDao.replaceAll(market)

        sharedPreferences.edit {
            putLong(KEY_CRYPTO_CACHED_AT_MILLIS, nowMillis)
        }
    }

    companion object {
        private const val KEY_STOCK_CACHED_AT_MILLIS = "stock_cached_at_millis"
        private const val KEY_CRYPTO_CACHED_AT_MILLIS = "crypto_cached_at_millis"
    }

    fun formatUtcMillis(millis: Long): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneOffset.UTC)

        return formatter.format(Instant.ofEpochMilli(millis))
    }
}