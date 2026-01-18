package com.davidcrespo.onewallet.di

import com.davidcrespo.onewallet.data.remote.crypto.BinanceApiClient
import com.davidcrespo.onewallet.data.remote.crypto.BinanceDataSource
import com.davidcrespo.onewallet.data.remote.fund.investing.InvestingApiClient
import com.davidcrespo.onewallet.data.remote.fund.investing.InvestingDataSource
import com.davidcrespo.onewallet.data.remote.fund.quefondos.QueFondosApiClient
import com.davidcrespo.onewallet.data.remote.fund.quefondos.QueFondosDataSource
import com.davidcrespo.onewallet.data.remote.rate.TwelveDataApiClient
import com.davidcrespo.onewallet.data.remote.rate.TwelveDataDataSource
import com.davidcrespo.onewallet.data.remote.stock.FinnhubApiClient
import com.davidcrespo.onewallet.data.remote.stock.FinnhubDataSource
import com.davidcrespo.onewallet.data.remote.telegram.TelegramApiClient
import org.koin.dsl.module

val dataSourceModule = module {

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
}
