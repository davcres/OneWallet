package com.davidcrespo.onewallet.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.davidcrespo.onewallet.BuildConfig
import com.davidcrespo.onewallet.data.local.cache.CurrencyCache
import com.davidcrespo.onewallet.data.local.cache.CurrencyCacheImpl
import com.davidcrespo.onewallet.data.local.cache.MarketCache
import com.davidcrespo.onewallet.data.local.cache.MarketCacheImpl
import com.davidcrespo.onewallet.data.local.cache.SymbolCache
import com.davidcrespo.onewallet.data.local.cache.SymbolCacheImpl
import com.davidcrespo.onewallet.data.local.database.AppDatabase
import com.davidcrespo.onewallet.data.remote.crypto.BinanceApiClient
import com.davidcrespo.onewallet.data.remote.crypto.BinanceApiConfig
import com.davidcrespo.onewallet.data.remote.crypto.BinanceDataSource
import com.davidcrespo.onewallet.data.remote.fund.investing.InvestingApiClient
import com.davidcrespo.onewallet.data.remote.fund.investing.InvestingApiConfig
import com.davidcrespo.onewallet.data.remote.fund.investing.InvestingDataSource
import com.davidcrespo.onewallet.data.remote.fund.quefondos.QueFondosApiClient
import com.davidcrespo.onewallet.data.remote.fund.quefondos.QueFondosApiConfig
import com.davidcrespo.onewallet.data.remote.fund.quefondos.QueFondosDataSource
import com.davidcrespo.onewallet.data.remote.rate.TwelveDataApiClient
import com.davidcrespo.onewallet.data.remote.rate.TwelveDataApiConfig
import com.davidcrespo.onewallet.data.remote.rate.TwelveDataDataSource
import com.davidcrespo.onewallet.data.remote.stock.FinnhubApiClient
import com.davidcrespo.onewallet.data.remote.stock.FinnhubApiConfig
import com.davidcrespo.onewallet.data.remote.stock.FinnhubDataSource
import com.davidcrespo.onewallet.data.remote.telegram.TelegramApiClient
import com.davidcrespo.onewallet.data.remote.telegram.TelegramApiConfig
import com.davidcrespo.onewallet.data.repository.FinancialRepositoryImpl
import com.davidcrespo.onewallet.data.repository.PortfolioRepositoryImpl
import com.davidcrespo.onewallet.domain.di.DispatcherProvider
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository
import com.davidcrespo.onewallet.domain.usecase.historical.GetMonthlyHistoryUseCase
import com.davidcrespo.onewallet.domain.usecase.market.AddMarketAssetToPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.market.GetMarketAssetsUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.AddInvestmentToPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetInvestmentPriceUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetPortfolioItemsUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetUsdEurUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.RemovePortfolioItemUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.SaveMonthlyPortfolioUseCase
import com.davidcrespo.onewallet.presentation.historical.HistoricalViewModel
import com.davidcrespo.onewallet.presentation.market.MarketViewModel
import com.davidcrespo.onewallet.presentation.portfolio.CurrencyConverter
import com.davidcrespo.onewallet.presentation.portfolio.PortfolioViewModel
import com.davidcrespo.onewallet.presentation.widget.WidgetsRefreshWorker
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val FINNHUB = named("FINNHUB")
private val TWELVE_DATA = named("TWELVE_DATA")
private val BINANCE = named("BINANCE")
private val INVESTING = named("INVESTING")
private val QUE_FONDOS = named("QUE_FONDOS")
private val TELEGRAM = named("TELEGRAM")
private val FINNHUB_KEY = named("FINNHUB_KEY")
private val TWELVE_DATA_KEY = named("TWELVE_DATA_KEY")
private val TELEGRAM_API_KEY = named("TELEGRAM_API_KEY")
private val TELEGRAM_CHAT_ID = named("TELEGRAM_CHAT_ID")

val appModule = module {

    single {
        androidContext().getSharedPreferences("onewallet_prefs", Context.MODE_PRIVATE)
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "onewallet-db"
        )
        .build()
    }

    single<DispatcherProvider> { DispatcherProviderImpl() }

    worker { WidgetsRefreshWorker(get(), get()) }

    single { get<AppDatabase>().portfolioDao() }
    single { get<AppDatabase>().stockMarketDao() }
    single { get<AppDatabase>().cryptoMarketDao() }

    // Keys
    single(FINNHUB_KEY) { BuildConfig.FINNHUB_API_KEY }
    single(TWELVE_DATA_KEY) { BuildConfig.TWELVE_DATA_API_KEY }
    single(TELEGRAM_API_KEY) { BuildConfig.TELEGRAM_API_KEY }
    single(TELEGRAM_CHAT_ID) { BuildConfig.TELEGRAM_CHAT_ID }


    // Base HttpClient (Common)
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = false
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 15000
                socketTimeoutMillis = 15000
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("KtorLog", message)
                    }
                }
                level = LogLevel.ALL
                filter { request ->
                    request.url.host != "api.telegram.org"
                }
            }

            expectSuccess = true
        }
    }

    // Finnhub HttpClient
    single(FINNHUB) {
        val base = get<HttpClient>()
        val apiKey = get<String>(FINNHUB_KEY)

        base.config {
            defaultRequest {
                url(FinnhubApiConfig.BASE_URL)
                url.parameters.append(FinnhubApiConfig.GetSymbols.TOKEN, apiKey)
            }
        }
    }

    // TwelveData HttpClient
    single(TWELVE_DATA) {
        val base = get<HttpClient>()
        val apiKey = get<String>(TWELVE_DATA_KEY)

        base.config {
            defaultRequest {
                url(TwelveDataApiConfig.BASE_URL)
                url.parameters.append(TwelveDataApiConfig.GetRate.API_KEY, apiKey)
            }
        }
    }

    // Binance HttpClient
    single(BINANCE) {
        val base = get<HttpClient>()

        base.config {
            defaultRequest { url(BinanceApiConfig.BASE_URL) }
        }
    }

    // Investing HttpClient
    single(INVESTING) {
        val base = get<HttpClient>()

        base.config {
            defaultRequest { url(InvestingApiConfig.BASE_URL) }
        }
    }

    // QueFondos HttpClient
    single(QUE_FONDOS) {
        val base = get<HttpClient>()

        base.config {
            defaultRequest { url(QueFondosApiConfig.BASE_URL) }
        }
    }

    // Telegram HttpClient
    single(TELEGRAM) {
        val base = get<HttpClient>()

        base.config {
            defaultRequest {
                url(TelegramApiConfig.BASE_URL)
            }
        }
    }

    single { FinnhubApiClient(get(FINNHUB)) }
    single { TwelveDataApiClient(get(TWELVE_DATA)) }
    single { BinanceApiClient(get(BINANCE)) }
    single { InvestingApiClient(get(INVESTING)) }
    single { QueFondosApiClient(get(QUE_FONDOS)) }
    single { TelegramApiClient(get(TELEGRAM), get(TELEGRAM_API_KEY), get(TELEGRAM_CHAT_ID)) }

    single { FinnhubDataSource(get()) }
    single { TwelveDataDataSource(get()) }
    single { BinanceDataSource(get()) }
    single { InvestingDataSource(get()) }
    single { QueFondosDataSource(get()) }

    single<SymbolCache> { SymbolCacheImpl(get(), get()) }
    single<CurrencyCache> { CurrencyCacheImpl(get(), get()) }
    single<MarketCache> { MarketCacheImpl(get(), get(), get(), get()) }

    single<FinancialRepository> { FinancialRepositoryImpl(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    single<PortfolioRepository> { PortfolioRepositoryImpl(get(), get()) }

    single { GetInvestmentPriceUseCase(get()) }
    single { GetMarketAssetsUseCase(get(), get()) }
    single { GetUsdEurUseCase(get()) }
    
    single { GetPortfolioItemsUseCase(get()) }
    single { AddInvestmentToPortfolioUseCase(get()) }
    single { AddMarketAssetToPortfolioUseCase(get()) }
    single { RemovePortfolioItemUseCase(get()) }
    
    single { SaveMonthlyPortfolioUseCase(get()) }
    single { GetMonthlyHistoryUseCase(get()) }

    single { CurrencyConverter() }

    viewModelOf(::PortfolioViewModel)
    viewModelOf(::MarketViewModel)
    viewModelOf(::HistoricalViewModel)
}