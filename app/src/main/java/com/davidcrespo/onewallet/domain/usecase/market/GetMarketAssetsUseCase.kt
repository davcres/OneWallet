package com.davidcrespo.onewallet.domain.usecase.market

import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import com.davidcrespo.onewallet.domain.repository.FinancialRepository

class GetMarketAssetsUseCase(private val repository: FinancialRepository) {
    private val FAVORITE_SYMBOLS = setOf(
        "AAPL", "AMZN", "GOOGL", "META", "MSFT", "META", "NVDA", "TSLA",
        "BTCEUR", "BTCUSD", "BTCUSDC", "BTCUSDCT", "ETHUSDC", "ETHUSDT"
    )

    suspend operator fun invoke(
        isCrypto: Boolean = false
    ): Result<List<Pair<Char, List<MarketAsset>>>> {
        val marketAssets = if (isCrypto) {
            repository.getCryptosSymbols()
        } else {
            repository.getStocksSymbols("US")
        }

        return marketAssets.mapCatching { assets ->
            val sorted = assets.sortedBy { it.symbol }

            val favorites = ArrayList<MarketAsset>()
            val assetsByInitial = linkedMapOf<Char, MutableList<MarketAsset>>() // keeps insertion order

            for (asset in sorted) {
                if (asset.symbol in FAVORITE_SYMBOLS) {
                    favorites += asset
                }

                val initial = asset.symbol.firstOrNull() ?: '#'
                assetsByInitial.getOrPut(initial) { ArrayList() }.add(asset)
            }

            buildList {
                add('★' to favorites)
                assetsByInitial.forEach { (k, v) -> add(k to v) }
            }
        }
    }
}