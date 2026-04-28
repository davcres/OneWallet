package com.davidcrespo.onewallet.domain.usecase.history

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository

class ImportHistoryUseCase(
    private val portfolioRepository: PortfolioRepository
) {
    suspend operator fun invoke(csvContent: String): Result<Unit> {
        return runCatching {
            val lines = csvContent.lines()
            if (lines.size < 2) return@runCatching // Header + at least one line
            
            val investments = lines.drop(1).filter { it.isNotBlank() }.map { line ->
                val parts = line.split(";")
                Investment(
                    symbol = parts[0],
                    name = parts[1],
                    quantity = parts[2].toDouble(),
                    price = parts[3].toDouble(),
                    previousPrice = parts[4].toDouble(),
                    currency = Currency(parts[5]),
                    type = InvestmentType.valueOf(parts[6]),
                    year = parts[7].toInt(),
                    month = parts[8].toInt()
                )
            }
            portfolioRepository.addOrUpdateItems(investments)
        }
    }
}
