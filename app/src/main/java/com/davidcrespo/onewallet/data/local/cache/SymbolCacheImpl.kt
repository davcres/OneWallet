package com.davidcrespo.onewallet.data.local.cache

import android.content.SharedPreferences
import androidx.core.content.edit
import com.davidcrespo.onewallet.data.local.database.portfolio.entities.InvestmentEntity
import com.davidcrespo.onewallet.data.local.database.portfolio.entities.toInvestmentEntity
import java.time.Clock
import java.util.concurrent.TimeUnit

class SymbolCacheImpl(
    private val sharedPreferences: SharedPreferences,
    private val clock: Clock
) : SymbolCache {

    override fun getCachedInvestmentIfValid(symbol: String, validCacheHours: Long): InvestmentEntity? {
        if (!isValid(symbol, validCacheHours)) return null

        return getCachedInvestment(symbol)
    }

    override fun getCachedInvestment(symbol: String): InvestmentEntity? {
        val raw = sharedPreferences.getString(valueKey(symbol), null) ?: return null
        return runCatching { raw.toInvestmentEntity() }.getOrNull()
    }

    override fun setCachedInvestment(investmentEntity: InvestmentEntity) {
        val symbol = investmentEntity.symbol
        val nowMillis = clock.millis()

        sharedPreferences.edit {
            putLong(cachedAtKey(symbol), nowMillis)
            putString(valueKey(symbol), investmentEntity.toString())
        }
    }

    override fun removeCachedInvestment(symbol: String) {
        sharedPreferences.edit {
            remove(cachedAtKey(symbol))
            remove(valueKey(symbol))
        }
    }

    private fun isValid(symbol: String, validCacheHours: Long): Boolean {
        val nowMillis = clock.millis()
        val cachedAt = sharedPreferences.getLong(cachedAtKey(symbol), 0L)
        val cacheDurationMillis = TimeUnit.HOURS.toMillis(validCacheHours)
        val age = nowMillis - cachedAt
        return cachedAt > 0L && age in 0 until cacheDurationMillis
    }

    private fun valueKey(symbol: String) = "inv_$symbol"
    private fun cachedAtKey(symbol: String) = "inv_${symbol}_cached_at"
}
