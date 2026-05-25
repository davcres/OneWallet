package com.davidcrespo.onewallet.presentation.market.usMarket

import androidx.compose.runtime.Immutable
import com.davidcrespo.onewallet.presentation.models.MarketAssetView
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class UsMarketUiState(
    val marketAssets: ImmutableList<Pair<String, ImmutableList<MarketAssetView>>> = persistentListOf(),
    val filteredAssets: ImmutableList<Pair<String, ImmutableList<MarketAssetView>>> = persistentListOf(),
    val assetsToSaveToPortfolio: ImmutableList<MarketAssetView> = persistentListOf(),
    val searchQuery: String = "",
    val isCrypto: Boolean = false,
    val showGlobalMarketsCard: Boolean = false,
    val isLoading: Boolean = true
)

sealed interface UsMarketEffect {
    data object NavigateBack : UsMarketEffect
    data object NavigateToGlobalMarket : UsMarketEffect
}

sealed interface UsMarketIntent {
    data class LoadInitialData(val isCrypto: Boolean) : UsMarketIntent
    data class SearchQueryChanged(val query: String) : UsMarketIntent
    data class AddOneAsset(val marketAsset: MarketAssetView) : UsMarketIntent
    data class SelectAsset(val marketAsset: MarketAssetView) : UsMarketIntent
    data object SaveAssetsSelected : UsMarketIntent
    data object OpenGlobalMarket : UsMarketIntent
    data object CloseGlobalMarketCard : UsMarketIntent
}
