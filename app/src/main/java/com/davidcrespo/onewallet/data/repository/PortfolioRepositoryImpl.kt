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
                    dcaAmount = entity.dcaAmount,
                    dcaFrequency = entity.dcaFrequency,
                    dcaStartDate = entity.dcaStartDate,
                    dcaInitialInvestment = entity.dcaInitialInvestment
                )
            }
        }
    }

    override suspend fun addOrUpdateItem(stockInfo: StockInfo, quantity: Double) {
        val existingItem = dao.getItem(stockInfo.displaySymbol)
        val sortOrder = existingItem?.sortOrder ?: ((dao.getMaxSortOrder() ?: 0) + 1)
        // Preserve existing DCA settings if updating quantity
        val dcaAmount = existingItem?.dcaAmount ?: 0.0
        val dcaFrequency = existingItem?.dcaFrequency ?: "Mensual"
        val dcaStartDate = existingItem?.dcaStartDate
        val dcaInitialInvestment = existingItem?.dcaInitialInvestment ?: 0.0
        
        dao.insertOrUpdate(stockInfo.toEntity(quantity, sortOrder, dcaAmount, dcaFrequency, dcaStartDate, dcaInitialInvestment))
    }

    override suspend fun updateDcaSettings(
        stockInfo: StockInfo, 
        dcaAmount: Double, 
        dcaFrequency: String,
        dcaStartDate: Long?,
        dcaInitialInvestment: Double
    ) {
        val existingItem = dao.getItem(stockInfo.displaySymbol) ?: return
        dao.insertOrUpdate(stockInfo.toEntity(
            existingItem.quantity, 
            existingItem.sortOrder, 
            dcaAmount, 
            dcaFrequency, 
            dcaStartDate, 
            dcaInitialInvestment
        ))
    }

    override suspend fun removeItem(stockInfo: StockInfo) {
        // We need the entity to delete, but assuming we can delete by ID logic.
        // Actually delete takes entity. We can construct a dummy entity with the key or fetch it.
        // Or simpler, add deleteBySymbol to DAO.
        // For now, let's fetch and delete, or construct entity.
        // existingItem logic is safer.
        val existingItem = dao.getItem(stockInfo.displaySymbol)
        if (existingItem != null) {
            dao.delete(existingItem)
        }
    }

    override suspend fun updateOrder(items: List<PortfolioItem>) {
        // Update sortOrder for each item based on list index
        val updatedEntities = items.mapIndexed { index, item ->
            item.stockInfo.toEntity(
                item.quantity, 
                index + 1, 
                item.dcaAmount, 
                item.dcaFrequency,
                item.dcaStartDate,
                item.dcaInitialInvestment
            )
        }
        dao.updatePortfolioOrder(updatedEntities)
    }
}
