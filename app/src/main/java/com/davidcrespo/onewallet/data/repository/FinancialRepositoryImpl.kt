package com.davidcrespo.onewallet.data.repository

import com.davidcrespo.onewallet.data.remote.finnhub.FinnhubDataSource
import com.davidcrespo.onewallet.data.remote.finnhub.models.toDomain
import com.davidcrespo.onewallet.data.remote.twelveData.models.toDomain
import com.davidcrespo.onewallet.data.remote.twelveData.TwelveDataDataSource
import com.davidcrespo.onewallet.domain.model.twelveData.Price
import com.davidcrespo.onewallet.domain.model.finnhub.Quote
import com.davidcrespo.onewallet.domain.model.finnhub.Rate
import com.davidcrespo.onewallet.domain.model.finnhub.StockInfo
import com.davidcrespo.onewallet.domain.repository.FinancialRepository

class FinancialRepositoryImpl(
    private val twelveDataDataSource: TwelveDataDataSource,
    private val finnhubDataSource: FinnhubDataSource,
) : FinancialRepository {
    override suspend fun getPrice(symbol: String): Result<Price> {
        return try {
            val priceResponse = twelveDataDataSource.getPrice(symbol)
            Result.success(priceResponse.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSymbols(exchange: String): Result<List<StockInfo>> {
        return try {
            val quoteResponse = finnhubDataSource.getSymbols(exchange)
            Result.success(quoteResponse.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCryptoSymbols(exchange: String): Result<List<StockInfo>> {
        return try {
            val cryptoResponse = finnhubDataSource.getCryptoSymbols(exchange)
            Result.success(cryptoResponse.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getQuote(symbol: String): Result<Quote> {
        return try {
            val quoteResponse = finnhubDataSource.getPrice(symbol)
            Result.success(quoteResponse.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUsdEur(): Result<Rate> {
        return try {
            val rate = twelveDataDataSource.getUsdEur()
            Result.success(rate.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}