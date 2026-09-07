package com.davidcrespo.onewallet.data.local.cache

import android.content.SharedPreferences
import androidx.core.content.edit
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import java.time.Clock
import java.util.concurrent.TimeUnit

class CurrencyCacheImpl(
    private val sharedPreferences: SharedPreferences,
    private val clock: Clock
): CurrencyCache {

    override fun getCachedRateIfValid(symbol: String, validCacheHours: Long): Double? {
        val nowMillis = clock.millis()

        val cacheDurationMillis = TimeUnit.HOURS.toMillis(validCacheHours)

        val cachedAt = sharedPreferences.getLong(cachedAtKey(symbol), 0L)
        val cachedRate = sharedPreferences.getString(rateKey(symbol), null)?.toDoubleOrNull()

        val isCacheValid = cachedRate != null &&
                cachedAt > 0L &&
                (nowMillis - cachedAt) in 0 until cacheDurationMillis

        return cachedRate.takeIf { isCacheValid }
    }

    override fun setCachedRate(symbol: String, price: Double) {
        val nowMillis = clock.millis()

        sharedPreferences.edit {
            putLong(cachedAtKey(symbol), nowMillis)
            putString(rateKey(symbol), price.toString())
        }
    }

    override fun getSelectedCurrency(): Currency {
        val currencyName = sharedPreferences.getString(CURRENCY, null)
        return runCatching { Currency(currencyName ?: EUR) }
            .getOrDefault(Currency(EUR))
    }

    override fun setSelectedCurrency(currency: Currency) {
        sharedPreferences.edit {
            putString(CURRENCY, currency.code)
        }
    }

    private fun rateKey(symbol: String) = "rate_$symbol"
    private fun cachedAtKey(symbol: String) = "rate_${symbol}_cached_at"

    companion object {
        private const val CURRENCY = "currency"
    }
}