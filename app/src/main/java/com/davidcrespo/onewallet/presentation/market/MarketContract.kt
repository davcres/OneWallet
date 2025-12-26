package com.davidcrespo.onewallet.presentation.market

import com.davidcrespo.onewallet.domain.model.market.MarketAsset

data class MarketState(
    val marketAssets: List<Pair<Char, List<MarketAsset>>> = emptyList(),
    val filteredAssets: List<Pair<Char, List<MarketAsset>>> = emptyList(),
    val assetsToSaveToPortfolio: List<MarketAsset> = emptyList(),
    val searchQuery: String = "",
    val isCrypto: Boolean = false,
    val navigateBack: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed interface MarketIntent {
    data class LoadInitialData(val isCrypto: Boolean) : MarketIntent
    data class SearchQueryChanged(val query: String) : MarketIntent
    data class AddOneAsset(val marketAsset: MarketAsset) : MarketIntent
    data class SelectAsset(val marketAsset: MarketAsset) : MarketIntent
    data object SaveAssetsSelected : MarketIntent
}
