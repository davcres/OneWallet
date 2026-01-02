package com.davidcrespo.onewallet.domain.usecase.historical

import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository

class GetMonthlyHistoryUseCase(
    private val portfolioRepository: PortfolioRepository
) {
    suspend operator fun invoke(): Result<List<Investment>> {
        return runCatching {
            Result.success(
            portfolioRepository.getMonthsPortfolio()
            )
        }.getOrElse {
            Result.failure(it)
        }
    }
}