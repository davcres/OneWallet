package com.davidcrespo.onewallet.domain.repository

import com.davidcrespo.onewallet.domain.model.investment.Investment
import kotlinx.coroutines.flow.Flow

interface PortfolioRepository {
    suspend fun getPortfolioItems(): Flow<List<Investment>>
    suspend fun addOrUpdateItem(investment: Investment)
    suspend fun removeItem(investment: Investment, year: Int, month: Int)
}