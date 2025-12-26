package com.davidcrespo.onewallet.data.repository

import com.davidcrespo.onewallet.BuildConfig
import com.davidcrespo.onewallet.data.local.cache.MarketCache
import com.davidcrespo.onewallet.data.local.cache.RateCache
import com.davidcrespo.onewallet.data.local.cache.SymbolCache
import com.davidcrespo.onewallet.data.local.database.market.entities.toCryptoEntity
import com.davidcrespo.onewallet.data.local.database.market.entities.toDomain
import com.davidcrespo.onewallet.data.local.database.market.entities.toStockEntity
import com.davidcrespo.onewallet.data.local.database.portfolio.entities.toDomain
import com.davidcrespo.onewallet.data.remote.dto.toDomain
import com.davidcrespo.onewallet.data.remote.dto.toEntity
import com.davidcrespo.onewallet.data.remote.finnhub.FinnhubDataSource
import com.davidcrespo.onewallet.data.remote.finnhub.models.toDomain
import com.davidcrespo.onewallet.data.remote.twelveData.TwelveDataApiConfig.GetRate.USD_EUR
import com.davidcrespo.onewallet.data.remote.twelveData.TwelveDataDataSource
import com.davidcrespo.onewallet.data.remote.twelveData.models.toDomain
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import com.davidcrespo.onewallet.domain.model.rate.Rate
import com.davidcrespo.onewallet.domain.repository.FinancialRepository

class FinancialRepositoryImpl(
    private val twelveDataDataSource: TwelveDataDataSource,
    private val finnhubDataSource: FinnhubDataSource,
    private val symbolCache: SymbolCache,
    private val rateCache: RateCache,
    private val marketCache: MarketCache
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
            val cachedPrice = symbolCache.getCachedInvestmentIfValid(symbol, if (BuildConfig.DEBUG) 24*7 else 1)

            val investment = if (cachedPrice != null) {
                cachedPrice.toDomain()
            } else {
                val investmentDto = twelveDataDataSource.getCryptoPrice(symbol)
                symbolCache.setCachedInvestment(investmentDto.toEntity())
                investmentDto.toDomain()
            }


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

            Result.success(investment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getStockPrice(symbol: String): Result<Investment> {
        return try {
            val cachedInvestment = symbolCache.getCachedInvestmentIfValid(symbol, if (BuildConfig.DEBUG) 24*7 else 1)

            val investment = if (cachedInvestment != null) {
                cachedInvestment.toDomain()
            } else {
                val investmentDto = finnhubDataSource.getStockPrice(symbol)
                symbolCache.setCachedInvestment(investmentDto.toEntity())
                investmentDto.toDomain()
            }
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

            return Result.success(investment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getStocksSymbols(exchange: String): Result<List<MarketAsset>> {
        return runCatching {
            val cachedStocks = marketCache.getCachedStockMarketIfValid(if (BuildConfig.DEBUG) 24*7 else 24)

            val stocks = if (cachedStocks.isNotEmpty()) {
                cachedStocks.map { it.toDomain() }
            } else {
                val stocksResponse = finnhubDataSource.getStocksSymbols(exchange)
                marketCache.setCachedStockMarket(stocksResponse.mapNotNull { it.toStockEntity() })
                stocksResponse.mapNotNull { it.toDomain() }
            }

            Result.success(stocks)
        }.getOrElse {
            Result.failure(it)
        }
    }

    override suspend fun getCryptosSymbols(exchange: String): Result<List<MarketAsset>> {
        return runCatching {
            val cachedCryptos = marketCache.getCachedCryptoMarketIfValid(if (BuildConfig.DEBUG) 24*7 else 24)

            val cryptos = if (cachedCryptos.isNotEmpty()) {
                cachedCryptos.map { it.toDomain() }
            } else {
                val cryptosResponse = finnhubDataSource.getCryptoSymbols(exchange)
                val filteredCryptos = cryptosResponse.filter {
                    it.displaySymbol.endsWith("/EUR", ignoreCase = true) ||
                    it.displaySymbol.endsWith("/USDC", ignoreCase = true)
                }
                marketCache.setCachedCryptoMarket(filteredCryptos.map { it.toCryptoEntity() })
                filteredCryptos.map { it.toDomain() }
            }

            Result.success(cryptos)
        }.getOrElse {
            Result.failure(it)
        }
    }

    override suspend fun getUsdEur(): Result<Rate> {
        return runCatching {
            val cachedRate = rateCache.getCachedRateIfValid(USD_EUR, if (BuildConfig.DEBUG) 24*7 else 24)
            if (cachedRate != null) {
                Result.success(Rate(USD_EUR, cachedRate))
            } else {
                val rate = twelveDataDataSource.getUsdEur()
                rateCache.setCachedRate(rate.symbol, rate.rate)
                Result.success(rate.toDomain())
            }
        }.getOrElse {
            Result.failure(it)
        }
    }
}