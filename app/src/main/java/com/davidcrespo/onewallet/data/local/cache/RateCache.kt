package com.davidcrespo.onewallet.data.local.cache

interface RateCache {

    suspend fun getCachedRateIfValid(symbol: String, validCacheHours: Long): Double?
    suspend fun setCachedRate(symbol: String, price: Double)
}
