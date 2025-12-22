package com.davidcrespo.onewallet.data.local.cache

interface SymbolCache {

    suspend fun getCachedSymbolIfValid(symbol: String, validCacheHours: Long): Double?
    suspend fun setCachedSymbol(symbol: String, price: Double)
}
