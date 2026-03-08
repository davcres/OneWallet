package com.davidcrespo.onewallet.di

import com.davidcrespo.onewallet.domain.usecase.historical.GetMonthlyHistoryUseCase
import com.davidcrespo.onewallet.domain.usecase.market.AddMarketAssetToPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.market.GetGlobalMarketAssetsUseCase
import com.davidcrespo.onewallet.domain.usecase.market.GetUSMarketAssetsUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.AddInvestmentToPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetCurrencyRateUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetInvestmentPriceUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetPortfolioItemsUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.RemovePortfolioItemUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.SaveMonthlyPortfolioUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { GetInvestmentPriceUseCase(get()) }
    single { GetUSMarketAssetsUseCase(get(), get()) }
    single { GetGlobalMarketAssetsUseCase(get(), get()) }
    single { GetCurrencyRateUseCase(get()) }

    single { GetPortfolioItemsUseCase(get()) }
    single { AddInvestmentToPortfolioUseCase(get()) }
    single { AddMarketAssetToPortfolioUseCase(get()) }
    single { RemovePortfolioItemUseCase(get()) }

    single { SaveMonthlyPortfolioUseCase(get()) }
    single { GetMonthlyHistoryUseCase(get()) }
}
