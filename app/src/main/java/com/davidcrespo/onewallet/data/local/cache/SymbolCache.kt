package com.davidcrespo.onewallet.data.local.cache

import com.davidcrespo.onewallet.data.local.database.portfolio.entities.InvestmentEntity

interface SymbolCache {

    suspend fun getCachedInvestmentIfValid(symbol: String, validCacheHours: Long): InvestmentEntity?
    suspend fun setCachedInvestment(investmentEntity: InvestmentEntity)
}
