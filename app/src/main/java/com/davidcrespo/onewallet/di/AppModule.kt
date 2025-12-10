package com.davidcrespo.onewallet.di

import android.util.Log
import com.davidcrespo.onewallet.data.remote.finnhub.FinnhubApiClient
import com.davidcrespo.onewallet.data.remote.finnhub.FinnhubDataSource
import com.davidcrespo.onewallet.data.remote.twelveData.TwelveDataApiClient
import com.davidcrespo.onewallet.data.remote.twelveData.TwelveDataDataSource
import com.davidcrespo.onewallet.data.repository.FinancialRepositoryImpl
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import com.davidcrespo.onewallet.domain.usecase.GetPriceUseCase
import com.davidcrespo.onewallet.domain.usecase.GetQuoteUseCase
import com.davidcrespo.onewallet.domain.usecase.GetSymbolsUseCase
import com.davidcrespo.onewallet.presentation.viewmodels.PriceViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

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
            }

            expectSuccess = true
        }
    }

    single { TwelveDataApiClient(get()) }
    single { FinnhubApiClient(get()) }

    single { TwelveDataDataSource(get()) }
    single { FinnhubDataSource(get()) }

    single<FinancialRepository> { FinancialRepositoryImpl(get(), get()) }

    single { GetPriceUseCase(get()) }
    single { GetSymbolsUseCase(get()) }
    single { GetQuoteUseCase(get()) }

    viewModelOf(::PriceViewModel)
}
