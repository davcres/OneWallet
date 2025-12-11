package com.davidcrespo.onewallet.data.repository

import com.davidcrespo.onewallet.data.local.database.dao.PortfolioDao
import com.davidcrespo.onewallet.data.local.database.entities.toDomain
import com.davidcrespo.onewallet.data.local.database.entities.toEntity
import com.davidcrespo.onewallet.domain.model.PortfolioItem
import com.davidcrespo.onewallet.domain.model.finnhub.StockInfo
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PortfolioRepositoryImpl(
    private val dao: PortfolioDao
) : PortfolioRepository {

    override fun getPortfolioItems(): Flow<List<PortfolioItem>> {
        return dao.getAllPortfolioItems().map { entities ->
            entities.map { entity ->
                PortfolioItem(
                    stockInfo = entity.toDomain(),
                    quantity = entity.quantity,
                    sortOrder = entity.sortOrder,
                    currentPrice = null
                )
            }
        }
    }

    override suspend fun addOrUpdateItem(stockInfo: StockInfo, quantity: Double) {
        val existingItem = dao.getItem(stockInfo.displaySymbol)
        val sortOrder = existingItem?.sortOrder ?: ((dao.getMaxSortOrder() ?: 0) + 1)
        
        dao.insertOrUpdate(stockInfo.toEntity(quantity, sortOrder))
    }

    override suspend fun removeItem(stockInfo: StockInfo) {
        val existingItem = dao.getItem(stockInfo.displaySymbol)
        if (existingItem != null) {
            dao.delete(existingItem)
        }
    }

    override suspend fun updateOrder(items: List<PortfolioItem>) {
        val updatedEntities = items.mapIndexed { index, item ->
            item.stockInfo.toEntity(
                item.quantity, 
                index + 1
            )
        }
        dao.updatePortfolioOrder(updatedEntities)
    }
}