package com.davidcrespo.onewallet.data.local.cache

import com.davidcrespo.onewallet.data.local.database.portfolio.entities.InvestmentEntity
import com.davidcrespo.onewallet.data.local.database.portfolio.entities.toInvestmentEntity
import com.russhwolf.settings.Settings
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.hours

class SymbolCacheImpl(
    private val settings: Settings,
    private val clock: Clock = Clock.System
) : LocalSymbolCache {

    override fun getCachedInvestmentIfValid(symbol: String, validCacheHours: Long): InvestmentEntity? {
        if (!isValid(symbol, validCacheHours)) return null

        return getCachedInvestment(symbol)
    }

    override fun getCachedInvestment(symbol: String): InvestmentEntity? {
        val raw = settings.getStringOrNull(valueKey(symbol)) ?: return null
        return runCatching { raw.toInvestmentEntity() }.getOrNull()
    }

    override fun setCachedInvestment(investmentEntity: InvestmentEntity) {
        val symbol = investmentEntity.symbol
        val nowMillis = clock.now().toEpochMilliseconds()

        settings.putLong(cachedAtKey(symbol), nowMillis)
        settings.putString(valueKey(symbol), investmentEntity.toString())
    }

    override fun removeCachedInvestment(symbol: String) {
        settings.remove(cachedAtKey(symbol))
        settings.remove(valueKey(symbol))
    }

    private fun isValid(symbol: String, validCacheHours: Long): Boolean {
        val nowMillis = clock.now().toEpochMilliseconds()
        val cachedAt = settings.getLong(cachedAtKey(symbol), 0L)
        val cacheDurationMillis = validCacheHours.hours.inWholeMilliseconds
        val age = nowMillis - cachedAt
        return cachedAt > 0L && age in 0 until cacheDurationMillis
    }

    private fun valueKey(symbol: String) = "inv_$symbol"
    private fun cachedAtKey(symbol: String) = "inv_${symbol}_cached_at"
}
