package com.davidcrespo.onewallet.di

import com.davidcrespo.onewallet.BuildConfig
import com.davidcrespo.onewallet.data.remote.ApiClient
import com.davidcrespo.onewallet.data.remote.FinancialDataSource
import com.davidcrespo.onewallet.data.repository.FinancialRepositoryImpl
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import com.davidcrespo.onewallet.domain.usecase.GetPriceUseCase
import com.davidcrespo.onewallet.presentation.viewmodels.PriceViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(DefaultRequest) {
                url {
                    protocol = URLProtocol.HTTPS
                    host = BuildConfig.BASE_URL
                }
            }
        }
    }

    single { ApiClient(get()) }
    single { FinancialDataSource(get()) }
    single<FinancialRepository> { FinancialRepositoryImpl(get()) }
    single { GetPriceUseCase(get()) }
    viewModel { PriceViewModel(get()) }

}
