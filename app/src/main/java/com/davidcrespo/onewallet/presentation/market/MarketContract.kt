package com.davidcrespo.onewallet.presentation.market

import androidx.compose.runtime.Immutable
import com.davidcrespo.onewallet.presentation.models.MarketAssetView
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class MarketUiState(
    val marketAssets: ImmutableList<Pair<Char, ImmutableList<MarketAssetView>>> = persistentListOf(),
    val filteredAssets: ImmutableList<Pair<Char, ImmutableList<MarketAssetView>>> = persistentListOf(),
    val assetsToSaveToPortfolio: ImmutableList<MarketAssetView> = persistentListOf(),
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
