package com.davidcrespo.onewallet.domain.repository

import com.davidcrespo.onewallet.domain.model.PortfolioItem
import com.davidcrespo.onewallet.domain.model.finnhub.StockInfo
import kotlinx.coroutines.flow.Flow

interface PortfolioRepository {
    fun getPortfolioItems(): Flow<List<PortfolioItem>>
    suspend fun addOrUpdateItem(stockInfo: StockInfo, quantity: Double)
    suspend fun removeItem(stockInfo: StockInfo)
    suspend fun updateOrder(items: List<PortfolioItem>)
}
