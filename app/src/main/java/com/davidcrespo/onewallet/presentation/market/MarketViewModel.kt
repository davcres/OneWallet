package com.davidcrespo.onewallet.presentation.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidcrespo.onewallet.domain.usecase.market.AddMarketAssetToPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.market.GetMarketAssetsUseCase
import com.davidcrespo.onewallet.presentation.models.MarketAssetView
import com.davidcrespo.onewallet.presentation.models.toDomain
import com.davidcrespo.onewallet.presentation.models.toUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MarketViewModel(
    private val getMarketAssetsUseCase: GetMarketAssetsUseCase,
    private val addMarketAssetToPortfolioUseCase: AddMarketAssetToPortfolioUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarketUiState())
    val uiState = _uiState.asStateFlow()

    fun handleIntent(intent: MarketIntent) {
        when (intent) {
            is MarketIntent.LoadInitialData -> getSymbols(intent.isCrypto)
            is MarketIntent.SearchQueryChanged -> updateSearchQuery(intent.query)
            is MarketIntent.AddOneAsset -> addOneAsset(intent.marketAsset)
            is MarketIntent.SelectAsset -> selectAsset(intent.marketAsset)
            is MarketIntent.SaveAssetsSelected -> saveAssetsSelected()
        }
    }

    private fun getSymbols(isCrypto: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getMarketAssetsUseCase(isCrypto)
                .onSuccess { marketAssets ->
                    val marketAssetsView = marketAssets.map { (letter, assets) ->
                        letter to assets.map { it.toUI() }
                    }
                    _uiState.update {
                        it.copy(
                            marketAssets = marketAssetsView,
                            filteredAssets = marketAssetsView,
                            isCrypto = isCrypto,
                            isLoading = false
                        )
                    }
                }.onFailure {
                    _uiState.update {
                        it.copy(
                            marketAssets = emptyList(),
                            filteredAssets = emptyList(),
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun updateSearchQuery(query: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = query,
                filteredAssets = filterAssets(currentState.marketAssets, query)
            )
        }
    }

    private fun addOneAsset(asset: MarketAssetView) {
        viewModelScope.launch {
            addMarketAssetToPortfolioUseCase(asset.toDomain(), _uiState.value.isCrypto)

            _uiState.update {
                it.copy(
                    searchQuery = "",
                    navigateBack = true
                )
            }
        }
    }

    private fun selectAsset(asset: MarketAssetView) {
        _uiState.update { state ->
            val current = state.assetsToSaveToPortfolio

            val newList = if (current.contains(asset)) {
                current.filterNot { it == asset }
            } else {
                current + asset
            }

            state.copy(assetsToSaveToPortfolio = newList)
        }
    }

    private fun saveAssetsSelected() {
        viewModelScope.launch {
            val list = _uiState.value.assetsToSaveToPortfolio
            list.forEach {
                addMarketAssetToPortfolioUseCase(it.toDomain(), _uiState.value.isCrypto)
            }
            _uiState.update {
                it.copy(
                    searchQuery = "",
                    assetsToSaveToPortfolio = listOf(),
                    navigateBack = true
                )
            }
        }
    }

    private fun filterAssets(
        allAssets: List<Pair<Char, List<MarketAssetView>>>,
        query: String
    ): List<Pair<Char, List<MarketAssetView>>> {
        val filtered: List<Pair<Char, List<MarketAssetView>>> =
            if (query.isBlank()) {
                allAssets
            } else {
                allAssets.map { (letter, assets) ->
                    letter to assets.filter { asset ->
                        asset.symbol.contains(query, ignoreCase = true) ||
                        asset.description?.contains(query, ignoreCase = true) == true ||
                        asset.figi?.contains(query, ignoreCase = true) == true
                    }
                }.filter { (_, assets) -> assets.isNotEmpty() }
            }

        return filtered
    }
}