package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.model.finnhub.StockInfo
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository

class UpdateDcaSettingsUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(
        stockInfo: StockInfo, 
        amount: Double, 
        frequency: String,
        startDate: Long?,
        initialInvestment: Double
    ) {
        repository.updateDcaSettings(stockInfo, amount, frequency, startDate, initialInvestment)
    }
}
