package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.model.finnhub.StockInfo
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository

class AddPortfolioItemUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(stockInfo: StockInfo, quantity: Double) {
        repository.addOrUpdateItem(stockInfo, quantity)
    }
}
