package com.davidcrespo.onewallet.data.local.cache

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.russhwolf.settings.Settings
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.hours

class CurrencyCacheImpl(
    private val settings: Settings,
    private val clock: Clock = Clock.System
): CurrencyCache {

    override fun getCachedRateIfValid(symbol: String, validCacheHours: Long): Double? {
        val nowMillis = clock.now().toEpochMilliseconds()
        val cacheDurationMillis = validCacheHours.hours.inWholeMilliseconds

        val cachedAt = settings.getLong(cachedAtKey(symbol), 0L)
        val cachedRate = settings.getStringOrNull(rateKey(symbol))?.toDoubleOrNull()

        val isCacheValid = cachedRate != null &&
                cachedAt > 0L &&
                (nowMillis - cachedAt) in 0 until cacheDurationMillis

        return cachedRate.takeIf { isCacheValid }
    }

    override fun setCachedRate(symbol: String, price: Double) {
        val nowMillis = clock.now().toEpochMilliseconds()
        settings.putLong(cachedAtKey(symbol), nowMillis)
        settings.putString(rateKey(symbol), price.toString())
    }

    override fun getSelectedCurrency(): Currency {
        val currencyName = settings.getStringOrNull(CURRENCY)
        return runCatching { Currency(currencyName ?: EUR) }
            .getOrDefault(Currency(EUR))
    }

    override fun setSelectedCurrency(currency: Currency) {
        settings.putString(CURRENCY, currency.code)
    }

    private fun rateKey(symbol: String) = "rate_$symbol"
    private fun cachedAtKey(symbol: String) = "rate_${symbol}_cached_at"

    companion object {
        private const val CURRENCY = "currency"
    }
}