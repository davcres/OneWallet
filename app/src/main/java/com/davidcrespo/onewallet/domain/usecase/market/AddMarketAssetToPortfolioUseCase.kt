package com.davidcrespo.onewallet.domain.usecase.market

import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import com.davidcrespo.onewallet.domain.model.market.toInvestment
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository
import java.time.LocalDate

class AddMarketAssetToPortfolioUseCase(private val repository: PortfolioRepository) {

    suspend operator fun invoke(marketAsset: MarketAsset, isCrypto: Boolean) {
        val now = LocalDate.now()
        val year = now.year
        val month = now.monthValue
        val investment = marketAsset.toInvestment(
            type = if (isCrypto) InvestmentType.CRYPTO else InvestmentType.STOCK,
            year = year,
            month = month
        )
        repository.addOrUpdateItem(investment)
    }
}
