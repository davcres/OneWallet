package com.davidcrespo.onewallet.data.repository

import com.davidcrespo.onewallet.data.remote.dto.toDomain
import com.davidcrespo.onewallet.data.remote.finnhub.FinnhubDataSource
import com.davidcrespo.onewallet.data.remote.finnhub.models.toDomain
import com.davidcrespo.onewallet.data.remote.twelveData.TwelveDataDataSource
import com.davidcrespo.onewallet.data.remote.twelveData.models.toDomain
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import com.davidcrespo.onewallet.domain.model.rate.Rate
import com.davidcrespo.onewallet.domain.repository.FinancialRepository

class FinancialRepositoryImpl(
    private val twelveDataDataSource: TwelveDataDataSource,
    private val finnhubDataSource: FinnhubDataSource
) : FinancialRepository {
    override suspend fun getInvestmentPrice(
        symbol: String,
        type: InvestmentType
    ): Result<Investment> {
        return when (type) {
            InvestmentType.STOCK -> getStockPrice(symbol)
            InvestmentType.CRYPTO -> getCryptoPrice(symbol)
            else -> throw IllegalArgumentException("Invalid investment type")
        }
    }

    private suspend fun getCryptoPrice(symbol: String): Result<Investment> {
        return try {
            val priceResponse = twelveDataDataSource.getCryptoPrice(symbol)
            //TODO***
            /*val currentPrice = priceResponse.price

            val item = historicalPortfolioDao.getItem(symbol)

            var previousClose: Double

            if (item != null) {
                val lastUpdated = item.cachedLastUpdated ?: 0L
                val cachedCurrent = item.cachedCurrentPrice ?: 0.0
                val cachedPrev = item.cachedPreviousClose ?: 0.0

                if (lastUpdated > 0) {
                    val lastDate = Instant.ofEpochMilli(lastUpdated)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    val today = LocalDate.now()

                    if (today.isAfter(lastDate)) {
                        // New day: yesterday's current is today's previous close
                        previousClose = cachedCurrent
                    } else {
                        // Same day: keep existing previous close
                        previousClose = cachedPrev
                    }
                } else {
                    // First time we don't have previous info for this item
                    previousClose = 0.0
                }

                // Save new state
                historicalPortfolioDao.insertOrUpdate(
                    item.copy(
                        cachedCurrentPrice = currentPrice,
                        cachedPreviousClose = previousClose,
                        cachedLastUpdated = System.currentTimeMillis()
                    )
                )
            }*/

            Result.success(priceResponse.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getStockPrice(symbol: String): Result<Investment> {
        return try {
            val stockPrice = finnhubDataSource.getStockPrice(symbol)
            //TODO***
            // Recupera el item de local y actualiza con el nuevo precio
            /*val item = historicalPortfolioDao.getItem(symbol)
            if (item != null) {
                val currentPrice = quoteResponse.price
                val previousClose = quoteResponse.previousPrice

                historicalPortfolioDao.insertOrUpdate(
                    item.copy(
                        cachedCurrentPrice = currentPrice,
                        cachedPreviousClose = previousClose,
                        cachedLastUpdated = System.currentTimeMillis()
                    )
                )
            }*/

            return Result.success(stockPrice.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getStocksSymbols(exchange: String): Result<List<MarketAsset>> {
        return try {
            val quoteResponse = finnhubDataSource.getStocksSymbols(exchange)
            Result.success(quoteResponse.mapNotNull { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCryptosSymbols(exchange: String): Result<List<MarketAsset>> {
        return try {
            val cryptoResponse = finnhubDataSource.getCryptoSymbols(exchange)
            val filterCryptos = cryptoResponse.filter {
                it.displaySymbol.endsWith("/EUR", ignoreCase = true) ||
                it.displaySymbol.endsWith("/USDC", ignoreCase = true)
            }
            Result.success(filterCryptos.map { it.toDomain() })
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