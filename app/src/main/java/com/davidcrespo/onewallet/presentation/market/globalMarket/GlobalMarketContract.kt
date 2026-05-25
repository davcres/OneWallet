package com.davidcrespo.onewallet.presentation.market.globalMarket

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.davidcrespo.onewallet.presentation.models.MarketAssetView
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class GlobalMarketUiState(
    val marketAssets: ImmutableList<Pair<String, ImmutableList<MarketAssetView>>>? = null,
    val assetsToSaveToPortfolio: ImmutableList<MarketAssetView> = persistentListOf(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    @StringRes val error: Int? = null
)

sealed interface GlobalMarketEffect {
    data object NavigateBack : GlobalMarketEffect
}

sealed interface GlobalMarketIntent {
    data class OnQueryChange(val query: String) : GlobalMarketIntent
    data class SearchByQuery(val query: String) : GlobalMarketIntent
    data class AddOneAsset(val marketAsset: MarketAssetView) : GlobalMarketIntent
    data class SelectAsset(val marketAsset: MarketAssetView) : GlobalMarketIntent
    data object SaveAssetsSelected : GlobalMarketIntent
    data object RetrySearch : GlobalMarketIntent
}
