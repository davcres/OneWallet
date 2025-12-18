package com.davidcrespo.onewallet.presentation.main


data class MainUiState(
    val screenStack: List<ScreenType> = listOf(ScreenType.Portfolio),
    val isCrypto: Boolean = false,
)

sealed interface MainIntent {
    data object OnBack : MainIntent
    data object NavigateToHistorical : MainIntent
    data class NavigateToMarket(val isCrypto: Boolean) : MainIntent
}
