package com.davidcrespo.onewallet.presentation.market.globalMarket

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.domain.usecase.market.AddMarketAssetToPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.market.GetGlobalMarketAssetsUseCase
import com.davidcrespo.onewallet.presentation.models.MarketAssetView
import com.davidcrespo.onewallet.presentation.models.toDomain
import com.davidcrespo.onewallet.presentation.models.toMarketAssetView
import com.davidcrespo.onewallet.presentation.models.toUI
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

class GlobalMarketViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val getGlobalMarketAssetsUseCase: GetGlobalMarketAssetsUseCase,
    private val addMarketAssetToPortfolioUseCase: AddMarketAssetToPortfolioUseCase,
) : ViewModel() {

    private val restoredAssetsToSave = savedStateHandle.get<String>("assetsToSaveToPortfolio")?.split(",")

    private val _uiState = MutableStateFlow(GlobalMarketUiState(
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
            initialValue = GlobalMarketUiState()
        )

    private val _effect = Channel<GlobalMarketEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: GlobalMarketIntent) {
        when (intent) {
            is GlobalMarketIntent.OnQueryChange -> updateQuery(intent.query)
            is GlobalMarketIntent.SearchByQuery -> getSymbolsByQuery(intent.query)
            is GlobalMarketIntent.AddOneAsset -> addOneAsset(intent.marketAsset)
            is GlobalMarketIntent.SelectAsset -> selectAsset(intent.marketAsset)
            is GlobalMarketIntent.SaveAssetsSelected -> saveAssetsSelected()
            is GlobalMarketIntent.RetrySearch -> retrySearch()
        }
    }

    private fun updateQuery(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query
            )
        }
    }

    private fun getSymbolsByQuery(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getGlobalMarketAssetsUseCase(query)
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
                            isLoading = false,
                        )
                    }
                }.onFailure {
                    _uiState.update {
                        it.copy(
                            marketAssets = persistentListOf(),
                            isLoading = false,
                            error = R.string.global_markets_request_limit
                        )
                    }
                }
        }
    }

    private fun addOneAsset(asset: MarketAssetView) {
        viewModelScope.launch {
            addMarketAssetToPortfolioUseCase(asset.toDomain(), false)

            _uiState.update {
                it.copy(
                    searchQuery = ""
                )
            }
            _effect.send(GlobalMarketEffect.NavigateBack)
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
                addMarketAssetToPortfolioUseCase(it.toDomain(), false)
            }
            _uiState.update {
                it.copy(
                    searchQuery = "",
                    assetsToSaveToPortfolio = persistentListOf()
                )
            }
            _effect.send(GlobalMarketEffect.NavigateBack)
        }
    }

    private fun retrySearch() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                marketAssets = null
            )
        }
    }
}