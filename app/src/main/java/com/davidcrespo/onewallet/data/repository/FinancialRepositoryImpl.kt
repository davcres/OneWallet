package com.davidcrespo.onewallet.data.repository

import com.davidcrespo.onewallet.data.local.cache.CurrencyCache
import com.davidcrespo.onewallet.data.local.cache.MarketCache
import com.davidcrespo.onewallet.data.local.cache.SymbolCache
import com.davidcrespo.onewallet.data.local.database.market.entities.toCryptoEntity
import com.davidcrespo.onewallet.data.local.database.market.entities.toDomain
import com.davidcrespo.onewallet.data.local.database.market.entities.toStockEntity
import com.davidcrespo.onewallet.data.local.database.portfolio.entities.toDomain
import com.davidcrespo.onewallet.data.remote.alphaVantage.AlphaVantageDataSource
import com.davidcrespo.onewallet.data.remote.alphaVantage.models.toDomain
import com.davidcrespo.onewallet.data.remote.binance.BinanceDataSource
import com.davidcrespo.onewallet.data.remote.binance.models.toDomain
import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.data.remote.dto.toDomain
import com.davidcrespo.onewallet.data.remote.dto.toEntity
import com.davidcrespo.onewallet.data.remote.extraEtf.ExtraEtfDataSource
import com.davidcrespo.onewallet.data.remote.finnhub.FinnhubDataSource
import com.davidcrespo.onewallet.data.remote.finnhub.models.toDomain
import com.davidcrespo.onewallet.data.remote.investing.InvestingDataSource
import com.davidcrespo.onewallet.data.remote.justEtf.JustEtfDataSource
import com.davidcrespo.onewallet.data.remote.marketstack.MarketstackDataSource
import com.davidcrespo.onewallet.data.remote.marketstack.models.toDomain
import com.davidcrespo.onewallet.data.remote.quefondos.QueFondosDataSource
import com.davidcrespo.onewallet.data.remote.twelveData.TwelveDataApiConfig.GetRate.USD_EUR
import com.davidcrespo.onewallet.data.remote.twelveData.TwelveDataDataSource
import com.davidcrespo.onewallet.data.remote.twelveData.models.toDomain
import com.davidcrespo.onewallet.domain.cache.CachePolicy
import com.davidcrespo.onewallet.domain.di.DispatcherProvider
import com.davidcrespo.onewallet.domain.logging.Telemetry
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.MarketType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import com.davidcrespo.onewallet.domain.model.rate.Rate
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import kotlinx.coroutines.withContext

class FinancialRepositoryImpl(
    private val twelveDataDataSource: TwelveDataDataSource,
    private val finnhubDataSource: FinnhubDataSource,
    private val alphaVantageDataSource: AlphaVantageDataSource,
    private val marketstackDataSource: MarketstackDataSource,
    private val binanceDataSource: BinanceDataSource,
    private val investingDataSource: InvestingDataSource,
    private val queFondosDataSource: QueFondosDataSource,
    private val justEtfDataSource: JustEtfDataSource,
    private val extraEtfDataSource: ExtraEtfDataSource,
    private val symbolCache: SymbolCache,
    private val currencyCache: CurrencyCache,
    private val marketCache: MarketCache,
    private val dispatcher: DispatcherProvider,
    private val cachePolicy: CachePolicy,
    private val telemetry: Telemetry,
) : FinancialRepository {

    override suspend fun getInvestmentPrice(
        symbol: String,
        type: InvestmentType,
        name: String,
        selectedCurrency: Currency?,
        marketType: MarketType?,
        investmentCurrency: Currency?
    ): Result<Investment> {
        return withContext(dispatcher.io) {
            when (type) {
                InvestmentType.STOCK -> getStockPrice(symbol, name, marketType, investmentCurrency)
                InvestmentType.CRYPTO -> getCryptoPrice(symbol)
                InvestmentType.FUND -> getFundPrice(symbol)
                InvestmentType.ETF -> getEtfPrice(symbol, selectedCurrency)
                else -> Result.failure(IllegalArgumentException("Invalid investment type: $type"))
            }
        }
    }

    private suspend fun getCryptoPrice(symbol: String): Result<Investment> {
        return runCatching {
            symbolCache.getCachedInvestmentIfValid(symbol, cachePolicy.cryptoHours)
                ?.toDomain()
                ?.let { return@runCatching it }

            val dto = binanceDataSource.getCryptoPrice(symbol)
            val valid = dto.takeIf { it.isValidPrice() }
                ?: throw IllegalStateException("No se pudo obtener el precio de $symbol")

            telemetry.log("(Binance) get $symbol from remote ${valid.price} ${valid.currency}")
            symbolCache.setCachedInvestment(valid.toEntity())
            valid.toDomain()
        }
    }

    private suspend fun getStockPrice(symbol: String, name: String, marketType: MarketType?, currency: Currency?): Result<Investment> =
        runCatching {
            symbolCache.getCachedInvestmentIfValid(symbol, cachePolicy.stockHours)
                ?.toDomain()
                ?.let { return@runCatching it }

            val country = marketType ?: MarketType.GLOBAL

            when (country) {
                MarketType.US -> {
                    tryFetch(symbol, "Finnhub") { finnhubDataSource.getStockPrice(symbol, name) }
                        ?: throw IllegalStateException("No se pudo obtener el precio del fondo")
                }
                MarketType.GLOBAL -> {
                    tryFetch(symbol, "Finnhub") { finnhubDataSource.getStockPrice(symbol, name) }
                        ?: tryFetch(symbol, "Alpha Vantage") { alphaVantageDataSource.getStockPrice(symbol, name, currency ?: Currency.USD) }
                        ?: tryFetch(symbol, "Marketstack") { marketstackDataSource.getStockPrice(symbol, name) }
                        ?: throw IllegalStateException("No se pudo obtener el precio del fondo")
                }
            }
        }

    private suspend fun getFundPrice(isin: String): Result<Investment> =
        runCatching {
            symbolCache.getCachedInvestmentIfValid(isin, cachePolicy.fundHours)
                ?.toDomain()
                ?.let { return@runCatching it }

            tryFetch(isin, "Investing.com") { investingDataSource.getFundPrice(isin) }
                ?: tryFetch(isin, "QueFondos.com") { queFondosDataSource.getFundPrice(isin, InvestmentType.FUND) }
                ?: throw IllegalStateException("No se pudo obtener el precio del fondo")
        }

    private suspend fun getEtfPrice(isin: String, selectedCurrency: Currency?): Result<Investment> {
        return runCatching {
            symbolCache.getCachedInvestmentIfValid(isin, cachePolicy.etfHours)
                ?.toDomain()
                ?.let { return@runCatching it }

            val currency = selectedCurrency ?: Currency.EUR

            tryFetch(isin, "JustETF.com (detail)") { justEtfDataSource.getEtfDetail(isin, currency) }
                ?: tryFetch(isin, "ExtraETF.com") { extraEtfDataSource.getEtfPrice(isin) }
                ?: tryFetch(isin, "QueFondos.com") { queFondosDataSource.getFundPrice(isin, InvestmentType.ETF) }
                ?: tryFetch(isin, "JustETF.com (price)", false) { justEtfDataSource.getEtfPrice(isin, currency) }
                ?: throw IllegalStateException("No se pudo obtener el precio del ETF")
        }
    }

    override suspend fun getStocksSymbols(exchange: String): Result<List<MarketAsset>> =
        withContext(dispatcher.io) {
            runCatching {
                val cached = marketCache.getCachedStockMarketIfValid(cachePolicy.marketHours)

                if (cached.isNotEmpty()) {
                    cached.map { it.toDomain() }
                } else {
                    val response = finnhubDataSource.getStocksSymbols(exchange)
                    telemetry.log("(Finnhub) get stock market from remote ${response.size}")
                    val entities = response.mapNotNull { it.toStockEntity() }
                    marketCache.setCachedStockMarket(entities)
                    response.mapNotNull { it.toDomain() }
                }
            }
        }

    override suspend fun getStocksSymbolsByQuery(query: String): Result<List<MarketAsset>> =
        withContext(dispatcher.io) {
            runCatching {
                val response = alphaVantageDataSource.getStocksSymbolsByQuery(query)
                telemetry.log("(Alpha Vantage) get symbols from remote $query - ${response.size}")

                val filtered = response.filter { asset ->
                    Currency.entries.any { currencies ->
                        asset.currency.equals(currencies.text, ignoreCase = true)
                    }
                }

                filtered.map { it.toDomain() }

                if (filtered.isNotEmpty()) {
                    Result.success(filtered.map { it.toDomain() })
                } else {
                    val response = marketstackDataSource.getStocksSymbolsByQuery(query)
                    telemetry.log("(Marketstack) get symbols from remote $query - ${response.size}")

                    val filtered = response.distinctBy { it.ticker }

                    Result.success(filtered.map { it.toDomain() })
                }
            }.getOrElse {
                it.printStackTrace()

                val response = marketstackDataSource.getStocksSymbolsByQuery(query)
                telemetry.log("(Marketstack) get symbols from remote $query - ${response.size}")

                val filtered = response.distinctBy { it.ticker }

                Result.success(filtered.map { it.toDomain() })
            }
        }

    override suspend fun getCryptosSymbols(allowedCurrencies: Set<String>): Result<List<MarketAsset>> =
        withContext(dispatcher.io) {
            runCatching {
                val cached = marketCache.getCachedCryptoMarketIfValid(cachePolicy.marketHours)

                if (cached.isNotEmpty()) {
                    cached.map { it.toDomain() }
                } else {
                    val response = binanceDataSource.getCryptoSymbols()
                    telemetry.log("(Binance) get crypto market from remote ${response.size}")

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
                val cached = currencyCache.getCachedRateIfValid(USD_EUR, cachePolicy.rateHours)
                if (cached != null) {
                    Rate(USD_EUR, cached)
                } else {
                    val rate = twelveDataDataSource.getUsdEur()
                    telemetry.log("(TwelveData) get USD/EUR from remote ${rate.rate}")
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

    private suspend fun tryFetch(
        isin: String,
        source: String,
        validateName: Boolean = true,
        fetch: suspend () -> InvestmentDto?
    ): Investment? {
        return runCatching {
            val inv = fetch()
            val valid = inv?.takeIf { if (validateName) { it.isValidName() && it.isValidPrice() } else { it.isValidPrice()} }
            if (valid != null) {
                telemetry.log("$source get $isin succeed ${valid.price} ${valid.currency}")
                symbolCache.setCachedInvestment(valid.toEntity())
            }
            valid?.toDomain()
        }.getOrElse {
            it.printStackTrace()
            null
        }
    }
}