package com.davidcrespo.onewallet.data.repository

import com.davidcrespo.onewallet.BuildConfig
import com.davidcrespo.onewallet.data.local.cache.CurrencyCache
import com.davidcrespo.onewallet.data.local.cache.MarketCache
import com.davidcrespo.onewallet.data.local.cache.SymbolCache
import com.davidcrespo.onewallet.data.local.database.market.entities.toCryptoEntity
import com.davidcrespo.onewallet.data.local.database.market.entities.toDomain
import com.davidcrespo.onewallet.data.local.database.market.entities.toStockEntity
import com.davidcrespo.onewallet.data.local.database.portfolio.entities.toDomain
import com.davidcrespo.onewallet.data.remote.crypto.BinanceDataSource
import com.davidcrespo.onewallet.data.remote.crypto.models.toDomain
import com.davidcrespo.onewallet.data.remote.dto.toDomain
import com.davidcrespo.onewallet.data.remote.dto.toEntity
import com.davidcrespo.onewallet.data.remote.fund.investing.InvestingDataSource
import com.davidcrespo.onewallet.data.remote.fund.quefondos.QueFondosDataSource
import com.davidcrespo.onewallet.data.remote.rate.TwelveDataApiConfig.GetRate.USD_EUR
import com.davidcrespo.onewallet.data.remote.rate.TwelveDataDataSource
import com.davidcrespo.onewallet.data.remote.rate.models.toDomain
import com.davidcrespo.onewallet.data.remote.stock.FinnhubDataSource
import com.davidcrespo.onewallet.data.remote.stock.models.toDomain
import com.davidcrespo.onewallet.data.remote.telegram.TelegramDataSource
import com.davidcrespo.onewallet.domain.di.DispatcherProvider
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import com.davidcrespo.onewallet.domain.model.rate.Rate
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import kotlinx.coroutines.withContext

class FinancialRepositoryImpl(
    private val twelveDataDataSource: TwelveDataDataSource,
    private val finnhubDataSource: FinnhubDataSource,
    private val binanceDataSource: BinanceDataSource,
    private val investingDataSource: InvestingDataSource,
    private val queFondosDataSource: QueFondosDataSource,
    private val telegramDataSource: TelegramDataSource,
    private val symbolCache: SymbolCache,
    private val currencyCache: CurrencyCache,
    private val marketCache: MarketCache,
    private val dispatcher: DispatcherProvider
) : FinancialRepository {
    val validCacheHours: Long = if (BuildConfig.DEBUG) 24 * 7 else 1

    override suspend fun getInvestmentPrice(
        symbol: String,
        type: InvestmentType,
        name: String
    ): Result<Investment> {
        return withContext(dispatcher.io) {
            when (type) {
                InvestmentType.STOCK -> getStockPrice(symbol, name)
                InvestmentType.CRYPTO -> getCryptoPrice(symbol)
                InvestmentType.FUND -> getFundPrice(symbol)
                else -> throw IllegalArgumentException("Invalid investment type")
            }
        }
    }

    private suspend fun getCryptoPrice(symbol: String): Result<Investment> =
        runCatching {
            val cached = symbolCache.getCachedInvestmentIfValid(symbol, validCacheHours)
            if (cached != null) {
                cached.toDomain()
            } else {
                val dto = binanceDataSource.getCryptoPrice(symbol)
                telegramDataSource.sendMessage("(Binance) get $symbol from remote")
                symbolCache.setCachedInvestment(dto.toEntity())
                dto.toDomain()
            }
        }

    private suspend fun getStockPrice(symbol: String, name: String): Result<Investment> =
        runCatching {
            val cached = symbolCache.getCachedInvestmentIfValid(symbol, validCacheHours)
            if (cached != null) {
                cached.toDomain()
            } else {
                val dto = finnhubDataSource.getStockPrice(symbol, name)
                telegramDataSource.sendMessage("(Finnhub) get $symbol from remote")
                symbolCache.setCachedInvestment(dto.toEntity())
                dto.toDomain()
            }
        }

    private suspend fun getFundPrice(isin: String): Result<Investment> = runCatching {
        val cached = symbolCache.getCachedInvestmentIfValid(isin, validCacheHours)
        if (cached != null) return@runCatching cached.toDomain()

        // Try primary source (investing.com), fallback to secondary (quefondos.com) if invalid
        val dto = investingDataSource.getFundPrice(isin)
            .also { telegramDataSource.sendMessage("(Investing.com) get $isin from remote") }
            ?.takeUnless { it.name.isEmpty() || it.price == 0.0 }
            ?: queFondosDataSource.getFundPrice(isin)
            .also { telegramDataSource.sendMessage("(QueFondos.com) get $isin from remote") }

        // If still null or invalid, fail
        val validDto = dto?.takeIf { it.name.isNotEmpty() && it.price != 0.0 }
            ?: throw Exception("No se pudo obtener el precio del fondo")
                .also { telegramDataSource.sendMessage("(Investing.com) and (QueFondos.com) get $isin failed") }

        telegramDataSource.sendMessage("$isin succeed")

        // Cache only if it looks valid
        symbolCache.setCachedInvestment(validDto.toEntity())

        validDto.toDomain()
    }

    override suspend fun getStocksSymbols(exchange: String): Result<List<MarketAsset>> =
        withContext(dispatcher.io) {
            runCatching {
                val cached = marketCache.getCachedStockMarketIfValid(validCacheHours)

                if (cached.isNotEmpty()) {
                    cached.map { it.toDomain() }
                } else {
                    val response = finnhubDataSource.getStocksSymbols(exchange)
                    telegramDataSource.sendMessage("(Finnhub) get stock market from remote")
                    val entities = response.mapNotNull { it.toStockEntity() }
                    marketCache.setCachedStockMarket(entities)
                    response.mapNotNull { it.toDomain() }
                }
            }
        }

    override suspend fun getCryptosSymbols(allowedCurrencies: Set<String>): Result<List<MarketAsset>> =
        withContext(dispatcher.io) {
            runCatching {
                val cached = marketCache.getCachedCryptoMarketIfValid(validCacheHours)

                if (cached.isNotEmpty()) {
                    cached.map { it.toDomain() }
                } else {
                    val response = binanceDataSource.getCryptoSymbols()
                    telegramDataSource.sendMessage("(Binance) get crypto market from remote")

                    val filtered = response.filter { crypto ->
                        allowedCurrencies.any { currencies ->
                            crypto.symbol.endsWith(currencies, ignoreCase = true)
                        }
                    }

                    marketCache.setCachedCryptoMarket(filtered.map { it.toCryptoEntity() })
                    filtered.map { it.toDomain() }
                }
            }
        }

    override suspend fun getUsdEur(): Result<Rate> =
        withContext(dispatcher.io) {
            runCatching {
                val cached = currencyCache.getCachedRateIfValid(USD_EUR, validCacheHours)
                if (cached != null) {
                    Rate(USD_EUR, cached)
                } else {
                    val rate = twelveDataDataSource.getUsdEur()
                    telegramDataSource.sendMessage("(TwelveData) get USD/EUR from remote")
                    currencyCache.setCachedRate(rate.symbol, rate.rate)
                    rate.toDomain()
                }
            }
        }

    override fun getSelectedCurrency(): Currency =
        runCatching { currencyCache.getSelectedCurrency() }
            .getOrDefault(Currency.EUR)

    override fun setSelectedCurrency(currency: Currency) {
        currencyCache.setSelectedCurrency(currency)
    }
}