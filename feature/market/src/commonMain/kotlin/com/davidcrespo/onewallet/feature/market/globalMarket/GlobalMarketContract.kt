package com.davidcrespo.onewallet.feature.market.globalMarket

import androidx.compose.runtime.Immutable
import com.davidcrespo.onewallet.core.models.MarketAssetView
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.StringResource

@Immutable
data class GlobalMarketUiState(
    val marketAssets: ImmutableList<Pair<String, ImmutableList<MarketAssetView>>>? = null,
    val assetsToSaveToPortfolio: ImmutableList<MarketAssetView> = persistentListOf(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: StringResource? = null
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
