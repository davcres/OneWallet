package com.davidcrespo.onewallet.presentation.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.NavigateToHistorical -> {
                _uiState.value = _uiState.value.copy(
                    screenStack = _uiState.value.screenStack + ScreenType.Historical
                )
            }
            is MainIntent.NavigateToMarket -> {
                _uiState.value = _uiState.value.copy(
                    screenStack = _uiState.value.screenStack + ScreenType.Market,
                    isCrypto = intent.isCrypto
                )
            }
            is MainIntent.OnBack -> {
                if (_uiState.value.screenStack.size > 1) {
                    _uiState.value = _uiState.value.copy(
                        screenStack = _uiState.value.screenStack.dropLast(1)
                    )
                }
            }
        }
    }
}