package com.davidcrespo.onewallet.presentation.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import com.davidcrespo.onewallet.domain.usecase.market.AddMarketAssetToPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.market.GetMarketAssetsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MarketViewModel(
    private val getMarketAssetsUseCase: GetMarketAssetsUseCase,
    private val addMarketAssetToPortfolioUseCase: AddMarketAssetToPortfolioUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarketState())
    val uiState = _uiState.asStateFlow()

    fun handleIntent(intent: MarketIntent) {
        when (intent) {
            is MarketIntent.LoadInitialData -> getSymbols(intent.isCrypto)
            is MarketIntent.SearchQueryChanged -> updateSearchQuery(intent.query)
            is MarketIntent.SelectAsset -> selectAsset(intent.symbol)
        }
    }

    private fun getSymbols(isCrypto: Boolean) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = getMarketAssetsUseCase(isCrypto)
            result.onSuccess { symbols ->
                _uiState.update {
                    it.copy(
                        marketAssets = symbols,
                        filteredAssets = symbols,
                        isCrypto = isCrypto,
                        isLoading = false
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(marketAssets = emptyList(), filteredAssets = emptyList(), isLoading = false) }
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

    private fun selectAsset(asset: MarketAsset) {
        viewModelScope.launch {
            addMarketAssetToPortfolioUseCase(asset, _uiState.value.isCrypto)

            _uiState.update {
                it.copy(
                    searchQuery = "",
                    navigateBack = true
                )
            }
        }
    }

    private fun filterAssets(allAssets: List<MarketAsset>, query: String): List<MarketAsset> {
        val filtered = if (query.isBlank()) {
            allAssets
        } else {
            allAssets.filter {
                it.symbol.contains(query, ignoreCase = true) ||
                it.description?.contains(query, ignoreCase = true) == true ||
                it.figi?.contains(query, ignoreCase = true) == true
            }
        }

        return filtered
    }
}