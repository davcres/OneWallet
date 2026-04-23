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
import com.davidcrespo.onewallet.data.remote.dto.CurrencyDto
import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.data.remote.dto.toDomain
import com.davidcrespo.onewallet.data.remote.dto.toDto
import com.davidcrespo.onewallet.data.remote.dto.toEntity
import com.davidcrespo.onewallet.data.remote.extraEtf.ExtraEtfDataSource
import com.davidcrespo.onewallet.data.remote.finnhub.FinnhubDataSource
import com.davidcrespo.onewallet.data.remote.finnhub.models.toDomain
import com.davidcrespo.onewallet.data.remote.investing.InvestingDataSource
import com.davidcrespo.onewallet.data.remote.justEtf.JustEtfDataSource
import com.davidcrespo.onewallet.data.remote.marketstack.MarketstackDataSource
import com.davidcrespo.onewallet.data.remote.marketstack.models.toDomain
import com.davidcrespo.onewallet.data.remote.quefondos.QueFondosDataSource
import com.davidcrespo.onewallet.data.remote.twelveData.TwelveDataDataSource
import com.davidcrespo.onewallet.data.remote.twelveData.models.toDomain
import com.davidcrespo.onewallet.data.remote.yahooFinance.YahooFinanceDataSource
import com.davidcrespo.onewallet.data.remote.yahooFinance.models.toDomain
import com.davidcrespo.onewallet.domain.cache.CachePolicy
import com.davidcrespo.onewallet.domain.di.DispatcherProvider
import com.davidcrespo.onewallet.domain.logging.Telemetry
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.DataSource
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.MarketType
import com.davidcrespo.onewallet.domain.model.investment.UNKNOWN
import com.davidcrespo.onewallet.domain.model.investment.USD
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import com.davidcrespo.onewallet.domain.model.rate.Rate
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import kotlinx.coroutines.withContext

class FinancialRepositoryImpl(
    private val twelveDataDataSource: TwelveDataDataSource,
    private val finnhubDataSource: FinnhubDataSource,
    private val alphaVantageDataSource: AlphaVantageDataSource,
    private val marketstackDataSource: MarketstackDataSource,
    private val yahooFinanceDataSource: YahooFinanceDataSource,
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
        investmentCurrency: Currency?,
        preferredApi: DataSource?
    ): Result<Investment> {
        return withContext(dispatcher.io) {
            when (type) {
                InvestmentType.STOCK -> getStockPrice(symbol, name, marketType, investmentCurrency, preferredApi)
                InvestmentType.CRYPTO -> getCryptoPrice(symbol)
                InvestmentType.FUND -> getFundPrice(symbol, preferredApi)
                InvestmentType.ETF -> getEtfPrice(symbol, selectedCurrency, preferredApi)
                else -> Result.failure(IllegalArgumentException("Invalid investment type: $type"))
            }
        }
    }

    private suspend fun getCryptoPrice(symbol: String): Result<Investment> {
        return runCatching {
            symbolCache.getCachedInvestmentIfValid(symbol, cachePolicy.cryptoHours)
                ?.toDomain()
                ?.let { return@runCatching it }

            tryFetch(symbol, DataSource.BINANCE) { binanceDataSource.getCryptoPrice(symbol) }
                ?: throw IllegalStateException("No se pudo obtener el precio de $symbol")
        }
    }

    private suspend fun getStockPrice(symbol: String, name: String, marketType: MarketType?, currency: Currency?, preferredApi: DataSource?): Result<Investment> =
        runCatching {
            val cachedValid = symbolCache.getCachedInvestmentIfValid(symbol, cachePolicy.stockHours)
            cachedValid?.toDomain()?.let { return@runCatching it }

            val apiToTryFirst = preferredApi ?: symbolCache.getCachedInvestment(symbol)?.preferredApi

            val country = marketType ?: MarketType.GLOBAL

            when (country) {
                MarketType.US -> {
                    tryFetch(symbol, DataSource.FINNHUB) { finnhubDataSource.getStockPrice(symbol, name) }
                        ?: throw IllegalStateException("No se pudo obtener el precio del fondo")
                }
                MarketType.GLOBAL -> {
                    val apis = listOf<Pair<DataSource, suspend () -> InvestmentDto?>>(
                        DataSource.YAHOO_FINANCE to { yahooFinanceDataSource.getStockPrice(symbol, name) },
                        DataSource.FINNHUB to { finnhubDataSource.getStockPrice(symbol, name) },
                        DataSource.ALPHA_VANTAGE to { alphaVantageDataSource.getStockPrice(symbol, name, currency?.toDto() ?: CurrencyDto(USD)) },
                        DataSource.MARKETSTACK to { marketstackDataSource.getStockPrice(symbol, name) }
                    )
                    tryFetchFromSequence(symbol, apis, apiToTryFirst)
                        ?: throw IllegalStateException("No se pudo obtener el precio del fondo")
                }
            }
        }

    private suspend fun getFundPrice(isin: String, preferredApi: DataSource?): Result<Investment> =
        runCatching {
            val cachedValid = symbolCache.getCachedInvestmentIfValid(isin, cachePolicy.fundHours)
            cachedValid?.toDomain()?.let { return@runCatching it }

            val apiToTryFirst = preferredApi ?: symbolCache.getCachedInvestment(isin)?.preferredApi

            val apis = listOf<Pair<DataSource, suspend () -> InvestmentDto?>>(
                DataSource.INVESTING_COM to { investingDataSource.getFundPrice(isin) },
                DataSource.QUE_FONDOS to { queFondosDataSource.getFundPrice(isin, InvestmentType.FUND) }
            )

            tryFetchFromSequence(isin, apis, apiToTryFirst)
                ?: throw IllegalStateException("No se pudo obtener el precio del fondo")
        }

    private suspend fun getEtfPrice(isin: String, selectedCurrency: Currency?, preferredApi: DataSource?): Result<Investment> {
        return runCatching {
            val cachedValid = symbolCache.getCachedInvestmentIfValid(isin, cachePolicy.etfHours)
            cachedValid?.toDomain()?.let { return@runCatching it }

            val apiToTryFirst = preferredApi ?: symbolCache.getCachedInvestment(isin)?.preferredApi

            val currency = selectedCurrency?.toDto() ?: CurrencyDto(EUR)

            val apis = listOf<Triple<DataSource, Boolean, suspend () -> InvestmentDto?>>(
                Triple(DataSource.JUST_ETF_DETAIL, true) { justEtfDataSource.getEtfDetail(isin, currency) },
                Triple(DataSource.EXTRA_ETF, true) { extraEtfDataSource.getEtfPrice(isin) },
                Triple(DataSource.QUE_FONDOS, true) { queFondosDataSource.getFundPrice(isin, InvestmentType.ETF) },
                Triple(DataSource.JUST_ETF_PRICE, false) { justEtfDataSource.getEtfPrice(isin, currency) }
            )

            if (apiToTryFirst != null) {
                val preferred = apis.find { it.first == apiToTryFirst }
                if (preferred != null) {
                    tryFetch(isin, preferred.first, preferred.second, preferred.third)?.let { return@runCatching it }
                }
            }

            for (api in apis) {
                if (api.first == apiToTryFirst) continue
                tryFetch(isin, api.first, api.second, api.third)?.let { return@runCatching it }
            }

            throw IllegalStateException("No se pudo obtener el precio del ETF")
        }
    }

    private suspend fun tryFetchFromSequence(
        isin: String,
        apis: List<Pair<DataSource, suspend () -> InvestmentDto?>>,
        preferredApi: DataSource?
    ): Investment? {
        if (preferredApi != null) {
            val preferred = apis.find { it.first == preferredApi }
            if (preferred != null) {
                tryFetch(isin, preferred.first, fetch = preferred.second)?.let { return it }
            }
        }

        for ((source, fetch) in apis) {
            if (source == preferredApi) continue
            tryFetch(isin, source, fetch = fetch)?.let { return it }
        }

        return null
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
                val response = yahooFinanceDataSource.getStocksSymbolsByQuery(query)
                telemetry.log("(Yahoo Finance) get symbols from remote $query - ${response.size}")

                if (false&&response.isNotEmpty()) {
                    Result.success(response.map { it.toDomain() })
                } else {
                    val response = alphaVantageDataSource.getStocksSymbolsByQuery(query)
                    telemetry.log("(Alpha Vantage) get symbols from remote $query - ${response.size}")

                    if (response.isNotEmpty()) {
                        Result.success(response.mapNotNull { it.toDomain() })
                    } else {
                        val response = marketstackDataSource.getStocksSymbolsByQuery(query)
                        telemetry.log("(Marketstack) get symbols from remote $query - ${response.size}")

                        val filtered = response.distinctBy { it.ticker }

                        Result.success(filtered.map { it.toDomain() })
                    }
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

    override suspend fun getRate(from: String, to: String): Result<Rate> =
        withContext(dispatcher.io) {
            runCatching {
                if (from == to || from == UNKNOWN || to == UNKNOWN) {
                    return@runCatching Rate("$from/$to", 1.0)
                }
                val rateSymbol = "$from/$to"
                val cached = currencyCache.getCachedRateIfValid(rateSymbol, cachePolicy.rateHours)
                if (cached != null) {
                    Rate(rateSymbol, cached)
                } else {
                    val rate = twelveDataDataSource.getRate(from, to)
                    telemetry.log("(TwelveData) get $from/$to from remote ${rate.rate}")
                    currencyCache.setCachedRate(rate.symbol, rate.rate)
                    rate.toDomain()
                }
            }
        }

    override fun getSelectedCurrency(): Currency =
        runCatching { currencyCache.getSelectedCurrency() }
            .getOrDefault(Currency(EUR))

    override fun setSelectedCurrency(currency: Currency) {
        currencyCache.setSelectedCurrency(currency)
    }

    private suspend fun tryFetch(
        isin: String,
        source: DataSource,
        validateName: Boolean = true,
        fetch: suspend () -> InvestmentDto?
    ): Investment? {
        return runCatching {
            val inv = fetch()
            val valid = inv?.takeIf { if (validateName) { it.isValidName() && it.isValidPrice() } else { it.isValidPrice()} }
                ?.copy(preferredApi = source)
            if (valid != null) {
                telemetry.log("${source.value} get $isin succeed ${valid.price} ${valid.currency.code}")
                symbolCache.setCachedInvestment(valid.toEntity())
            }
            valid?.toDomain()
        }.getOrElse {
            it.printStackTrace()
            null
        }
    }
}