package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.MarketType
import com.davidcrespo.onewallet.domain.model.investment.isMarket
import com.davidcrespo.onewallet.domain.model.onboarding.SeedAsset
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import java.time.LocalDate

/**
 * Use case to seed the portfolio with initial data for a better onboarding experience.
 */
class SeedInitialPortfolioUseCase(
    private val getInvestmentPriceUseCase: GetInvestmentPriceUseCase,
    private val addInvestmentToPortfolioUseCase: AddInvestmentToPortfolioUseCase
) {
    suspend operator fun invoke(selectedCurrency: Currency) = supervisorScope {
        val now = LocalDate.now()
        val year = now.year
        val month = now.monthValue

        val deferreds = INITIAL_ASSETS.map { asset ->
            async { seedAsset(asset, selectedCurrency, year, month) }
        }

        val initialItems = deferreds.awaitAll().filterNotNull()
        if (initialItems.isNotEmpty()) {
            addInvestmentToPortfolioUseCase(initialItems)
        }
    }

    private suspend fun seedAsset(
        asset: SeedAsset,
        selectedCurrency: Currency,
        year: Int,
        month: Int
    ): Investment? {
        return if (asset.type.isMarket()) {
            getInvestmentPriceUseCase(
                symbol = asset.symbol,
                type = asset.type,
                name = asset.name,
                marketType = asset.marketType
            ).fold(
                onSuccess = { investment ->
                    investment.copy(
                        quantity = asset.initialQuantity,
                        year = year,
                        month = month
                    )
                },
                onFailure = {
                    null
                }
            )

        } else {
            Investment(
                symbol = asset.symbol,
                name = asset.name,
                quantity = asset.initialQuantity,
                price = 1.0,
                previousPrice = 0.0,
                currency = selectedCurrency,
                type = asset.type,
                year = year,
                month = month
            )
        }
    }

    companion object {
        private val INITIAL_ASSETS = listOf(
            SeedAsset(
                symbol = "BTCEUR",
                name = "BTCEUR",
                type = InvestmentType.CRYPTO,
                initialQuantity = 0.01
            ),
            SeedAsset(
                symbol = "GOOGL",
                name = "Alphabet Inc.",
                type = InvestmentType.STOCK,
                initialQuantity = 2.0,
                marketType = MarketType.US
            ),
            SeedAsset(
                symbol = "Cuenta Remunerada",
                name = "Cuenta Remunerada",
                type = InvestmentType.BANK,
                initialQuantity = 500.0
            )
        )
    }
}
