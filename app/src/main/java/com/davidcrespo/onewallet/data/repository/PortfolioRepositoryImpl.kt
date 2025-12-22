package com.davidcrespo.onewallet.data.repository

import com.davidcrespo.onewallet.data.local.database.portfolio.dao.PortfolioDao
import com.davidcrespo.onewallet.data.local.database.portfolio.entities.toDomain
import com.davidcrespo.onewallet.data.local.database.portfolio.entities.toEntity
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PortfolioRepositoryImpl(
    private val dao: PortfolioDao
) : PortfolioRepository {

    override suspend fun getPortfolioItems(): Flow<List<Investment>> {
        return dao.getLatestPortfolio().map { it.map { it.toDomain() } }
    }

    override suspend fun addOrUpdateItem(investment: Investment) {
        dao.insertOrUpdate(investment.toEntity())
    }

    override suspend fun removeItem(investment: Investment, year: Int, month: Int) {
        dao.deleteInvestment(investment.symbol, year, month)
    }
}