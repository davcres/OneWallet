package com.davidcrespo.onewallet.splash

import androidx.lifecycle.ViewModel
import com.davidcrespo.onewallet.domain.di.AppCoroutineScope
import com.davidcrespo.onewallet.domain.repository.OnboardingRepository
import com.davidcrespo.onewallet.domain.usecase.market.GetUSMarketAssetsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel(
    private val appScope: AppCoroutineScope,
    private val onboardingRepository: OnboardingRepository,
    private val getMarketAssetsUseCase: GetUSMarketAssetsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

    fun handleIntent(intent: SplashIntent) {
        when (intent) {
            is SplashIntent.LoadMarkets -> loadMarkets()
            is SplashIntent.IsOnboardingCompleted -> isOnboardingCompleted()
        }
    }

    private fun isOnboardingCompleted() {
        val isOnboardingCompleted = onboardingRepository.isOnboardingCompleted()
        val isPortfolioOnboardingCompleted = onboardingRepository.isPortfolioOnboardingCompleted()
        _uiState.update {
            it.copy(
                onboardingCompleted = isOnboardingCompleted,
                portfolioOnboardingCompleted = isPortfolioOnboardingCompleted
            )
        }
    }

    private fun loadMarkets() {
        appScope.scope.launch {
            launch { runCatching { getMarketAssetsUseCase(false) } }
            launch { runCatching { getMarketAssetsUseCase(true) } }
        }
    }
}
