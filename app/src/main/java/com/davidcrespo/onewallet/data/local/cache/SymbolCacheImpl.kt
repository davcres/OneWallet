package com.davidcrespo.onewallet.data.local.cache

import android.content.SharedPreferences
import androidx.core.content.edit
import java.time.Clock
import java.util.concurrent.TimeUnit

class SymbolCacheImpl(
    private val sharedPreferences: SharedPreferences,
): SymbolCache {

    override fun getCachedSymbolIfValid(symbol: String, validCacheHours: Long): Double? {
        val nowMillis = Clock.systemUTC().millis()

        val cacheDurationMillis = TimeUnit.HOURS.toMillis(validCacheHours)

        val timestampKey = "$symbol$KEY_CACHED_AT_MILLIS"
        val cachedAt = sharedPreferences.getLong(timestampKey, 0L)
        val cachedRate = sharedPreferences.getString(symbol, null)?.toDoubleOrNull()

        val isCacheValid = cachedRate != null &&
                cachedAt > 0L &&
                (nowMillis - cachedAt) in 0 until cacheDurationMillis

        return if (isCacheValid) {
            cachedRate
        } else {
            null
        }
    }

    override fun setCachedSymbol(symbol: String, price: Double) {
        val nowMillis = Clock.systemUTC().millis()

        val timestampKey = "$symbol$KEY_CACHED_AT_MILLIS"

        sharedPreferences.edit {
            putLong(timestampKey, nowMillis)
            putString(symbol, price.toString())
        }
    }

    companion object {
        private const val KEY_CACHED_AT_MILLIS = "_cached_at_millis"
    }
}