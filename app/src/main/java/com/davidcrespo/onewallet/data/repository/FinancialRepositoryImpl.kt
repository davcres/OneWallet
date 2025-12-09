package com.davidcrespo.onewallet.data.repository

import com.davidcrespo.onewallet.data.remote.FinancialDataSource
import com.davidcrespo.onewallet.domain.model.Price
import com.davidcrespo.onewallet.domain.repository.FinancialRepository

class FinancialRepositoryImpl(private val dataSource: FinancialDataSource) : FinancialRepository {
    override suspend fun getPrice(symbol: String): Result<Price> {
        return try {
            val priceResponse = dataSource.getPrice(symbol)
            Result.success(Price(priceResponse.price.toBigDecimal()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
