package com.davidcrespo.onewallet.domain.usecase.market

import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import com.davidcrespo.onewallet.domain.repository.FinancialRepository

class GetMarketAssetsUseCase(private val repository: FinancialRepository) {
    suspend operator fun invoke(isCrypto: Boolean = false): Result<List<MarketAsset>> {
        val marketAssets = if (isCrypto) {
            repository.getCryptosSymbols("BINANCE")
        } else {
            repository.getStocksSymbols("US")
        }
        return marketAssets.map { it.sortedBy { it.symbol } }

    }
}