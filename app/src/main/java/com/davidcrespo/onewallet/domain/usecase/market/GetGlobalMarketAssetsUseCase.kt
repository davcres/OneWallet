package com.davidcrespo.onewallet.domain.usecase.market

import com.davidcrespo.onewallet.domain.di.DispatcherProvider
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import kotlinx.coroutines.withContext

class GetGlobalMarketAssetsUseCase(
    private val repository: FinancialRepository,
    private val dispatcher: DispatcherProvider
) {

    suspend operator fun invoke(
        query: String
    ): Result<List<Pair<String, List<MarketAsset>>>> {
        val marketAssets = repository.getStocksSymbolsByQuery(query)

        return marketAssets.mapCatching { assets ->
            withContext(dispatcher.default) { // Move to Default in case the list is too long to avoid lag.
                buildList {
                    add("⌕" to assets)
                }
            }
        }
    }
}