package com.davidcrespo.onewallet.domain.usecase.history

import com.davidcrespo.onewallet.core.extensions.toSpanishCsvFormat
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository

class ExportHistoryUseCase(
    private val portfolioRepository: PortfolioRepository
) {
    suspend operator fun invoke(): Result<String> {
        return runCatching {
            val history = portfolioRepository.getMonthsPortfolio()
            val csv = StringBuilder()
            // Header
            csv.append("Symbol;Name;Quantity;Price;PreviousPrice;Currency;Type;Year;Month;Category\n")
            history.forEach {
                val quantity = it.quantity.toSpanishCsvFormat()
                val price = it.price.toSpanishCsvFormat()
                val previousPrice = it.previousPrice.toSpanishCsvFormat()
                csv.append("${it.symbol};${it.name};$quantity;$price;$previousPrice;${it.currency.code};${it.type.name};${it.year};${it.month};${it.category.id}\n")
            }
            csv.toString()
        }
    }
}
