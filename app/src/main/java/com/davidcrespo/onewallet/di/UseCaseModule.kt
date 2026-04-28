package com.davidcrespo.onewallet.di

import com.davidcrespo.onewallet.domain.usecase.appRoot.GetThemeUseCase
import com.davidcrespo.onewallet.domain.usecase.appRoot.SetThemeUseCase
import com.davidcrespo.onewallet.domain.usecase.history.ExportHistoryUseCase
import com.davidcrespo.onewallet.domain.usecase.history.GetMonthlyHistoryUseCase
import com.davidcrespo.onewallet.domain.usecase.history.ImportHistoryUseCase
import com.davidcrespo.onewallet.domain.usecase.market.AddMarketAssetToPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.market.GetGlobalMarketAssetsUseCase
import com.davidcrespo.onewallet.domain.usecase.market.GetUSMarketAssetsUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.AddInvestmentToPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.ClearPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetCurrencyRateUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetInvestmentPriceUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetPortfolioItemsUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.RefreshPortfolioPricesUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.RemovePortfolioItemUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.SaveMonthlyPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.SeedInitialPortfolioUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { GetInvestmentPriceUseCase(get()) }
    single { SeedInitialPortfolioUseCase(get(), get()) }
    single { GetUSMarketAssetsUseCase(get(), get()) }
    single { GetGlobalMarketAssetsUseCase(get(), get()) }
    single { GetCurrencyRateUseCase(get()) }

    single { GetPortfolioItemsUseCase(get()) }
    single { RefreshPortfolioPricesUseCase(get(), get(), get(), get()) }
    single { ClearPortfolioUseCase(get(), get()) }
    single { AddInvestmentToPortfolioUseCase(get()) }
    single { AddMarketAssetToPortfolioUseCase(get()) }
    single { RemovePortfolioItemUseCase(get()) }

    single { SaveMonthlyPortfolioUseCase(get()) }
    single { GetMonthlyHistoryUseCase(get()) }
    single { ImportHistoryUseCase(get()) }
    single { ExportHistoryUseCase(get()) }

    single { GetThemeUseCase(get()) }
    single { SetThemeUseCase(get()) }
}
