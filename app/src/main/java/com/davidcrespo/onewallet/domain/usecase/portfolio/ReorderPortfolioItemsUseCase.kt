package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.model.PortfolioItem
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository

class ReorderPortfolioItemsUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(items: List<PortfolioItem>) {
        repository.updateOrder(items)
    }
}
