package com.davidcrespo.onewallet.domain.repository

import com.davidcrespo.onewallet.domain.model.investment.Investment
import kotlinx.coroutines.flow.Flow

interface PortfolioRepository {
    fun getPortfolioItems(): Flow<List<Investment>>
    suspend fun addOrUpdateItem(investment: Investment)
    suspend fun removeItem(investment: Investment, year: Int, month: Int)
    suspend fun updateMonthPortfolio(year: Int, month: Int, investments: List<Investment>)
    suspend fun deleteMonthPortfolio(year: Int, month: Int)
    suspend fun getMonthsPortfolio(): List<Investment>
}