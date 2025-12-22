package com.davidcrespo.onewallet.data.local.cache

interface SymbolCache {

    fun getCachedSymbolIfValid(symbol: String, validCacheHours: Long): Double?
    fun setCachedSymbol(symbol: String, price: Double)
}
