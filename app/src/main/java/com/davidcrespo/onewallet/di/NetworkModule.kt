package com.davidcrespo.onewallet.di

import android.util.Log
import com.davidcrespo.onewallet.data.remote.alphaVantage.AlphaVantageApiConfig
import com.davidcrespo.onewallet.data.remote.binance.BinanceApiConfig
import com.davidcrespo.onewallet.data.remote.extraEtf.ExtraEtfApiConfig
import com.davidcrespo.onewallet.data.remote.finnhub.FinnhubApiConfig
import com.davidcrespo.onewallet.data.remote.investing.InvestingApiConfig
import com.davidcrespo.onewallet.data.remote.justEtf.JustEtfApiConfig
import com.davidcrespo.onewallet.data.remote.quefondos.QueFondosApiConfig
import com.davidcrespo.onewallet.data.remote.telegram.TelegramApiConfig
import com.davidcrespo.onewallet.data.remote.twelveData.TwelveDataApiConfig
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
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {

    // Base HttpClient
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
                filter { req -> req.url.host != "api.telegram.org" }
            }

            expectSuccess = true
        }
    }

    // Finnhub client
    single(FINNHUB) {
        val base = get<HttpClient>()
        val apiKey = get<String>(FINNHUB_KEY)

        base.config {
            defaultRequest {
                url(FinnhubApiConfig.BASE_URL)
                url.parameters.append(FinnhubApiConfig.TOKEN, apiKey)
            }
        }
    }

    // Alpha Vantage client
    single(named("ALPHA_VANTAGE_JSON")) {
        Json {
            prettyPrint = false
            isLenient = true
            ignoreUnknownKeys = true
        }
    }
    single(named("ALPHA_VANTAGE_HTTP_CLIENT")) {
        val base = get<HttpClient>()

        base.config {
            defaultRequest {
                url(AlphaVantageApiConfig.BASE_URL)
            }
        }
    }
    single(ALPHA_VANTAGE) {
        AlphaVantageClient(
            client = get(named("ALPHA_VANTAGE_HTTP_CLIENT")),
            json = get(named("ALPHA_VANTAGE_JSON")),
            apiKeys = listOf(
                get(ALPHA_VANTAGE_KEY),
                get(ALPHA_VANTAGE_KEY_2),
                get(ALPHA_VANTAGE_KEY_3),
            ),
            context = get()
        )
    }

    // TwelveData client
    single(TWELVE_DATA) {
        val base = get<HttpClient>()
        val apiKey = get<String>(TWELVE_DATA_KEY)

        base.config {
            defaultRequest {
                url(TwelveDataApiConfig.BASE_URL)
                url.parameters.append(TwelveDataApiConfig.API_KEY, apiKey)
            }
        }
    }

    // Binance client
    single(BINANCE) {
        get<HttpClient>().config {
            defaultRequest { url(BinanceApiConfig.BASE_URL) }
        }
    }

    // Investing client
    single(INVESTING) {
        get<HttpClient>().config {
            defaultRequest { url(InvestingApiConfig.BASE_URL) }
        }
    }

    // QueFondos client
    single(QUE_FONDOS) {
        get<HttpClient>().config {
            defaultRequest { url(QueFondosApiConfig.BASE_URL) }
        }
    }

    // ExtraETF client
    single(JUST_ETF) {
        get<HttpClient>().config {
            defaultRequest {
                url(JustEtfApiConfig.BASE_URL)
                headers.append("User-Agent", "Mozilla/5.0") // To make it appear as if it's a request from a browser
            }
        }
    }

    // ExtraETF client
    single(EXTRA_ETF) {
        get<HttpClient>().config {
            defaultRequest {
                url(ExtraEtfApiConfig.BASE_URL)
                headers.append("User-Agent", "Mozilla/5.0") // To make it appear as if it's a request from a browser
            }
        }
    }

    // Telegram client
    single(TELEGRAM) {
        get<HttpClient>().config {
            defaultRequest { url(TelegramApiConfig.BASE_URL) }
        }
    }
}
