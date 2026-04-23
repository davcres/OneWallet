package com.davidcrespo.onewallet.data.local.cache

import com.davidcrespo.onewallet.data.local.database.portfolio.entities.InvestmentEntity

interface SymbolCache {

    fun getCachedInvestmentIfValid(symbol: String, validCacheHours: Long): InvestmentEntity?
    fun getCachedInvestment(symbol: String): InvestmentEntity?
    fun setCachedInvestment(investmentEntity: InvestmentEntity)
}
