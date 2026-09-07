package com.davidcrespo.onewallet.domain.usecase.portfolio

import kotlinx.coroutines.flow.first

class ClearPortfolioUseCase(
    private val getPortfolioItemsUseCase: GetPortfolioItemsUseCase,
    private val removePortfolioItemUseCase: RemovePortfolioItemUseCase
) {
    suspend operator fun invoke() {
        getPortfolioItemsUseCase().first().forEach { item ->
            removePortfolioItemUseCase(item)
        }
    }
}
