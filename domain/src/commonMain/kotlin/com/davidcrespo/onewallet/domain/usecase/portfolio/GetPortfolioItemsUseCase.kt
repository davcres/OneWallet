package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.Flow

class GetPortfolioItemsUseCase(private val repository: PortfolioRepository) {
    operator fun invoke(): Flow<List<Investment>> = repository.getPortfolioItems()
}
