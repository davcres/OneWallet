package com.davidcrespo.onewallet.domain.usecase.history

import com.davidcrespo.onewallet.domain.repository.PortfolioRepository

class ExportHistoryUseCase(
    private val portfolioRepository: PortfolioRepository
) {
    suspend operator fun invoke(): Result<String> {
        return runCatching {
            val history = portfolioRepository.getMonthsPortfolio()
            val csv = StringBuilder()
            // Header
            csv.append("Symbol;Name;Quantity;Price;PreviousPrice;Currency;Type;Year;Month\n")
            history.forEach {
                csv.append("${it.symbol};${it.name};${it.quantity};${it.price};${it.previousPrice};${it.currency.code};${it.type.name};${it.year};${it.month}\n")
            }
            csv.toString()
        }
    }
}
