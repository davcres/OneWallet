package com.davidcrespo.onewallet.feature.market.usMarket

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidcrespo.onewallet.domain.usecase.market.AddMarketAssetToPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.market.GetUSMarketAssetsUseCase
import com.davidcrespo.onewallet.core.models.MarketAssetView
import com.davidcrespo.onewallet.core.models.toDomain
import com.davidcrespo.onewallet.core.models.toMarketAssetView
import com.davidcrespo.onewallet.core.models.toUI
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsMarketViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val getUSMarketAssetsUseCase: GetUSMarketAssetsUseCase,
    private val addMarketAssetToPortfolioUseCase: AddMarketAssetToPortfolioUseCase,
) : ViewModel() {

    private val restoredAssetsToSave = savedStateHandle.get<String>("assetsToSaveToPortfolio")?.split(",")

    private val _uiState = MutableStateFlow(UsMarketUiState(
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
            initialValue = UsMarketUiState()
        )

    private val _effect = Channel<UsMarketEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: UsMarketIntent) {
        when (intent) {
            is UsMarketIntent.LoadInitialData -> getSymbols(intent.isCrypto)
            is UsMarketIntent.SearchQueryChanged -> updateSearchQuery(intent.query)
            is UsMarketIntent.AddOneAsset -> addOneAsset(intent.marketAsset)
            is UsMarketIntent.SelectAsset -> selectAsset(intent.marketAsset)
            is UsMarketIntent.SaveAssetsSelected -> saveAssetsSelected()
            is UsMarketIntent.CloseGlobalMarketCard -> closeGlobalMarketCard()
            is UsMarketIntent.OpenGlobalMarket -> {
                viewModelScope.launch {
                    _effect.send(UsMarketEffect.NavigateToGlobalMarket)
                }
            }
        }
    }

    private fun getSymbols(isCrypto: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getUSMarketAssetsUseCase(isCrypto)
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
                            isLoading = false,
                            showGlobalMarketsCard = true
                        )
                    }
                }.onFailure {
                    _uiState.update {
                        it.copy(
                            marketAssets = persistentListOf(),
                            filteredAssets = persistentListOf(),
                            isLoading = false,
                            showGlobalMarketsCard = true
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
                    searchQuery = ""
                )
            }
            _effect.send(UsMarketEffect.NavigateBack)
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
                    assetsToSaveToPortfolio = persistentListOf()
                )
            }
            _effect.send(UsMarketEffect.NavigateBack)
        }
    }

    private fun filterAssets(
        allAssets: ImmutableList<Pair<String, ImmutableList<MarketAssetView>>>,
        query: String
    ): ImmutableList<Pair<String, ImmutableList<MarketAssetView>>> {
        val filtered: ImmutableList<Pair<String, ImmutableList<MarketAssetView>>> =
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

    private fun closeGlobalMarketCard() {
        _uiState.update {
            it.copy(
                showGlobalMarketsCard = false,
            )
        }
    }
}