package com.davidcrespo.onewallet.presentation.market

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidcrespo.onewallet.domain.usecase.market.AddMarketAssetToPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.market.GetMarketAssetsUseCase
import com.davidcrespo.onewallet.presentation.models.MarketAssetView
import com.davidcrespo.onewallet.presentation.models.toDomain
import com.davidcrespo.onewallet.presentation.models.toMarketAssetView
import com.davidcrespo.onewallet.presentation.models.toUI
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MarketViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val getMarketAssetsUseCase: GetMarketAssetsUseCase,
    private val addMarketAssetToPortfolioUseCase: AddMarketAssetToPortfolioUseCase,
) : ViewModel() {

    private val restoredAssetsToSave = savedStateHandle.get<String>("assetsToSaveToPortfolio")?.split(",")

    private val _uiState = MutableStateFlow(MarketUiState(
        searchQuery = savedStateHandle["searchQuery"] ?: "",
        assetsToSaveToPortfolio = restoredAssetsToSave?.map { stringAsset ->
            stringAsset.toMarketAssetView()
        }?.toImmutableList() ?: persistentListOf()
    ))
    val uiState = _uiState
        .onEach { state ->
            savedStateHandle["searchQuery"] = state.searchQuery
            savedStateHandle["assetsToSaveToPortfolio"] = state.assetsToSaveToPortfolio.joinToString(",") { it.toString() }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = MarketUiState()
        )

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
                    val marketAssetsView =
                        marketAssets
                            .map { (letter, assets) ->
                                letter to assets.map { it.toUI() }.toImmutableList()
                            }
                            .toImmutableList()
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
                            marketAssets = persistentListOf(),
                            filteredAssets = persistentListOf(),
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
            }.toImmutableList()

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
                    assetsToSaveToPortfolio = persistentListOf(),
                    navigateBack = true
                )
            }
        }
    }

    private fun filterAssets(
        allAssets: ImmutableList<Pair<Char, ImmutableList<MarketAssetView>>>,
        query: String
    ): ImmutableList<Pair<Char, ImmutableList<MarketAssetView>>> {
        val filtered: ImmutableList<Pair<Char, ImmutableList<MarketAssetView>>> =
            if (query.isBlank()) {
                allAssets
            } else {
                allAssets.map { (letter, assets) ->
                    letter to assets.filter { asset ->
                        asset.symbol.contains(query, ignoreCase = true) ||
                        asset.description?.contains(query, ignoreCase = true) == true ||
                        asset.figi?.contains(query, ignoreCase = true) == true
                    }.toImmutableList()
                }.filter { (_, assets) -> assets.isNotEmpty() }
            }.toImmutableList()

        return filtered
    }
}