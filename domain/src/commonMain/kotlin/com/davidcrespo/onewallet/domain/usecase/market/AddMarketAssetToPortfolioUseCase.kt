package com.davidcrespo.onewallet.domain.usecase.market

import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import com.davidcrespo.onewallet.domain.model.market.toInvestment
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AddMarketAssetToPortfolioUseCase(
    private val repository: PortfolioRepository,
    private val clock: Clock = Clock.System
) {

    suspend operator fun invoke(marketAsset: MarketAsset, isCrypto: Boolean) {
        val now = clock.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val year = now.year
        val month = now.monthNumber
        val investment = marketAsset.toInvestment(
            type = if (isCrypto) InvestmentType.CRYPTO else InvestmentType.STOCK,
            year = year,
            month = month
        )
        repository.addOrUpdateItem(investment)
    }
}
