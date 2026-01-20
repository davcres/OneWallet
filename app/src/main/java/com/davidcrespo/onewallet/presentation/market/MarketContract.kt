package com.davidcrespo.onewallet.presentation.market

import androidx.compose.runtime.Immutable
import com.davidcrespo.onewallet.presentation.models.MarketAssetView

@Immutable
data class MarketUiState(
    val marketAssets: List<Pair<Char, List<MarketAssetView>>> = emptyList(),
    val filteredAssets: List<Pair<Char, List<MarketAssetView>>> = emptyList(),
    val assetsToSaveToPortfolio: List<MarketAssetView> = emptyList(),
    val searchQuery: String = "",
    val isCrypto: Boolean = false,
    val navigateBack: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed interface MarketIntent {
    data class LoadInitialData(val isCrypto: Boolean) : MarketIntent
    data class SearchQueryChanged(val query: String) : MarketIntent
    data class AddOneAsset(val marketAsset: MarketAssetView) : MarketIntent
    data class SelectAsset(val marketAsset: MarketAssetView) : MarketIntent
    data object SaveAssetsSelected : MarketIntent
}
