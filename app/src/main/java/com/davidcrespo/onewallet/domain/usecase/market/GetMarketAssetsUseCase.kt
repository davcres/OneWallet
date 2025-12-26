package com.davidcrespo.onewallet.domain.usecase.market

import com.davidcrespo.onewallet.core.extensions.groupByInitial
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import com.davidcrespo.onewallet.domain.repository.FinancialRepository

class GetMarketAssetsUseCase(private val repository: FinancialRepository) {
    suspend operator fun invoke(isCrypto: Boolean = false): Result<List<Pair<Char, List<MarketAsset>>>> {
        val marketAssets = if (isCrypto) {
            repository.getCryptosSymbols()
        } else {
            repository.getStocksSymbols("US")
        }
        val assets = marketAssets.map { it.sortedBy { it.symbol } }.getOrDefault(emptyList())
        return Result.success(assets.groupByInitial({ it.symbol }))
    }
}