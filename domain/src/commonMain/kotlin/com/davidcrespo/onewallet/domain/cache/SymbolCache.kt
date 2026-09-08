package com.davidcrespo.onewallet.domain.cache

interface SymbolCache {
    fun removeCachedInvestment(symbol: String)
}
