package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.isManual
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.supervisorScope

class RefreshPortfolioPricesUseCase(
    private val getPortfolioItemsUseCase: GetPortfolioItemsUseCase,
    private val getInvestmentPriceUseCase: GetInvestmentPriceUseCase,
    private val financialRepository: FinancialRepository,
    private val saveMonthlyPortfolioUseCase: SaveMonthlyPortfolioUseCase
) {
    suspend operator fun invoke(): List<Pair<Investment, Double>> = supervisorScope {
        val selectedCurrency = financialRepository.getSelectedCurrency()
        val portfolioItems = getPortfolioItemsUseCase().first()

        val (manualItems, marketItems) = portfolioItems
            .distinctBy { it.symbol }
            .partition { it.type.isManual() }

        val updatedMarketItems = marketItems.map { item ->
            async {
                getInvestmentPriceUseCase.invoke(
                    symbol = item.symbol,
                    type = item.type,
                    name = item.name,
                    selectedCurrency = selectedCurrency,
                    investmentCurrency = item.currency,
                    preferredApi = item.preferredApi
                ).fold(
                    onSuccess = { api ->
                        val changePercent = api.previousPrice
                            .takeIf { it != 0.0 }
                            ?.let { ((api.price - it) / it) * 100.0 } ?: 0.0

                        val updatedItem = item.copy(
                            price = api.price,
                            previousPrice = api.previousPrice,
                            preferredApi = api.preferredApi
                        )
                        updatedItem to changePercent
                    },
                    onFailure = {
                        item to 0.0
                    }
                )
            }
        }.awaitAll()

        val allUpdated = updatedMarketItems.map { it.first } + manualItems
        if (updatedMarketItems.isNotEmpty()) {
            saveMonthlyPortfolioUseCase(allUpdated)
        }

        updatedMarketItems
    }
}
