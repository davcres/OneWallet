package com.davidcrespo.onewallet.data.repository

import com.davidcrespo.onewallet.BuildConfig
import com.davidcrespo.onewallet.data.local.cache.MarketCache
import com.davidcrespo.onewallet.data.local.cache.RateCache
import com.davidcrespo.onewallet.data.local.cache.SymbolCache
import com.davidcrespo.onewallet.data.local.database.market.entities.toCryptoEntity
import com.davidcrespo.onewallet.data.local.database.market.entities.toDomain
import com.davidcrespo.onewallet.data.local.database.market.entities.toStockEntity
import com.davidcrespo.onewallet.data.local.database.portfolio.entities.toDomain
import com.davidcrespo.onewallet.data.remote.crypto.BinanceDataSource
import com.davidcrespo.onewallet.data.remote.crypto.models.toDomain
import com.davidcrespo.onewallet.data.remote.dto.toDomain
import com.davidcrespo.onewallet.data.remote.dto.toEntity
import com.davidcrespo.onewallet.data.remote.rate.TwelveDataApiConfig.GetRate.USD_EUR
import com.davidcrespo.onewallet.data.remote.rate.TwelveDataDataSource
import com.davidcrespo.onewallet.data.remote.rate.models.toDomain
import com.davidcrespo.onewallet.data.remote.stock.FinnhubDataSource
import com.davidcrespo.onewallet.data.remote.stock.models.toDomain
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import com.davidcrespo.onewallet.domain.model.rate.Rate
import com.davidcrespo.onewallet.domain.repository.FinancialRepository

class FinancialRepositoryImpl(
    private val twelveDataDataSource: TwelveDataDataSource,
    private val finnhubDataSource: FinnhubDataSource,
    private val binanceDataSource: BinanceDataSource,
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
            val cachedInvestment = symbolCache.getCachedInvestmentIfValid(symbol, if (BuildConfig.DEBUG) 24*7 else 1)

            val investment = if (cachedInvestment != null) {
                cachedInvestment.toDomain()
            } else {
                val investmentDto = binanceDataSource.getCryptoPrice(symbol)
                symbolCache.setCachedInvestment(investmentDto.toEntity())
                investmentDto.toDomain()
            }

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

    override suspend fun getCryptosSymbols(): Result<List<MarketAsset>> {
        return runCatching {
            val cachedCryptos = marketCache.getCachedCryptoMarketIfValid(if (BuildConfig.DEBUG) 24*7 else 24)

            val cryptos = if (cachedCryptos.isNotEmpty()) {
                cachedCryptos.map { it.toDomain() }
            } else {
                val cryptosResponse = binanceDataSource.getCryptoSymbols()
                val filteredCryptos = cryptosResponse.filter {
                    it.symbol.endsWith("EUR", ignoreCase = true) ||
                    it.symbol.endsWith("USD", ignoreCase = true) ||
                    it.symbol.endsWith("USDC", ignoreCase = true) ||
                    it.symbol.endsWith("USDT", ignoreCase = true)
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