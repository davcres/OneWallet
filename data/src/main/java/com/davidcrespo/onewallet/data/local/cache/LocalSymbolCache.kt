package com.davidcrespo.onewallet.data.local.cache

import com.davidcrespo.onewallet.data.local.database.portfolio.entities.InvestmentEntity
import com.davidcrespo.onewallet.domain.cache.SymbolCache

interface LocalSymbolCache : SymbolCache {
    fun getCachedInvestmentIfValid(symbol: String, validCacheHours: Long): InvestmentEntity?
    fun getCachedInvestment(symbol: String): InvestmentEntity?
    fun setCachedInvestment(investmentEntity: InvestmentEntity)
}
