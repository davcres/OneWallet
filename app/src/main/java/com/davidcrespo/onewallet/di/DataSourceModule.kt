package com.davidcrespo.onewallet.di

import com.davidcrespo.onewallet.data.remote.binance.BinanceApiClient
import com.davidcrespo.onewallet.data.remote.binance.BinanceDataSource
import com.davidcrespo.onewallet.data.remote.extraEtf.ExtraEtfApiClient
import com.davidcrespo.onewallet.data.remote.extraEtf.ExtraEtfDataSource
import com.davidcrespo.onewallet.data.remote.justEtf.JustEtfApiClient
import com.davidcrespo.onewallet.data.remote.justEtf.JustEtfDataSource
import com.davidcrespo.onewallet.data.remote.investing.InvestingApiClient
import com.davidcrespo.onewallet.data.remote.investing.InvestingDataSource
import com.davidcrespo.onewallet.data.remote.quefondos.QueFondosApiClient
import com.davidcrespo.onewallet.data.remote.quefondos.QueFondosDataSource
import com.davidcrespo.onewallet.data.remote.twelveData.TwelveDataApiClient
import com.davidcrespo.onewallet.data.remote.twelveData.TwelveDataDataSource
import com.davidcrespo.onewallet.data.remote.finnhub.FinnhubApiClient
import com.davidcrespo.onewallet.data.remote.finnhub.FinnhubDataSource
import com.davidcrespo.onewallet.data.remote.telegram.TelegramApiClient
import com.davidcrespo.onewallet.data.remote.telegram.TelegramDataSource
import org.koin.dsl.module

val dataSourceModule = module {

    single { FinnhubApiClient(get(FINNHUB)) }
    single { TwelveDataApiClient(get(TWELVE_DATA)) }
    single { BinanceApiClient(get(BINANCE)) }
    single { InvestingApiClient(get(INVESTING)) }
    single { QueFondosApiClient(get(QUE_FONDOS)) }
    single { JustEtfApiClient(get(JUST_ETF)) }
    single { ExtraEtfApiClient(get(EXTRA_ETF)) }
    single { TelegramApiClient(get(TELEGRAM), get(TELEGRAM_API_KEY), get(TELEGRAM_CHAT_ID)) }

    single { FinnhubDataSource(get()) }
    single { TwelveDataDataSource(get()) }
    single { BinanceDataSource(get()) }
    single { InvestingDataSource(get()) }
    single { QueFondosDataSource(get()) }
    single { JustEtfDataSource(get()) }
    single { ExtraEtfDataSource(get()) }
    single { TelegramDataSource(get()) }
}
