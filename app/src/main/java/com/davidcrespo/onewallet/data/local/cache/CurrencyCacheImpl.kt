package com.davidcrespo.onewallet.data.local.cache

import android.content.SharedPreferences
import androidx.core.content.edit
import com.davidcrespo.onewallet.data.remote.telegram.TelegramApiClient
import com.davidcrespo.onewallet.domain.model.investment.Currency
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class CurrencyCacheImpl(
    private val sharedPreferences: SharedPreferences,
    private val telegramApiClient: TelegramApiClient
): CurrencyCache {

    override suspend fun getCachedRateIfValid(symbol: String, validCacheHours: Long): Double? {
        val nowMillis = Clock.systemUTC().millis()

        val cacheDurationMillis = TimeUnit.HOURS.toMillis(validCacheHours)

        val timestampKey = "$symbol$KEY_CACHED_AT_MILLIS"
        val cachedAt = sharedPreferences.getLong(timestampKey, 0L)
        val cachedRate = sharedPreferences.getString(symbol, null)?.toDoubleOrNull()

        val isCacheValid = cachedRate != null &&
                cachedAt > 0L &&
                (nowMillis - cachedAt) in 0 until cacheDurationMillis

        return if (isCacheValid) {
            //telegramApiClient.sendMessage("get $symbol from cache at ${formatUtcMillis(nowMillis)}")
            cachedRate
        } else {
            null
        }
    }

    override suspend fun setCachedRate(symbol: String, price: Double) {
        val nowMillis = Clock.systemUTC().millis()

        telegramApiClient.sendMessage("SET $symbol from cache at ${formatUtcMillis(nowMillis)}")

        val timestampKey = "$symbol$KEY_CACHED_AT_MILLIS"

        sharedPreferences.edit {
            putLong(timestampKey, nowMillis)
            putString(symbol, price.toString())
        }
    }

    override fun getSelectedCurrency(): Currency {
        val currencyName = sharedPreferences.getString(CURRENCY, null)

        return currencyName?.let { Currency.valueOf(it) } ?: Currency.EUR
    }

    override fun setSelectedCurrency(currency: Currency) {
        sharedPreferences.edit {
            putString(CURRENCY, currency.name)
        }
    }

    companion object {
        private const val KEY_CACHED_AT_MILLIS = "_cached_at_millis"
        private const val CURRENCY = "currency"
    }

    fun formatUtcMillis(millis: Long): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneOffset.UTC)

        return formatter.format(Instant.ofEpochMilli(millis))
    }
}