package com.davidcrespo.onewallet.presentation.splash

import androidx.lifecycle.ViewModel
import com.davidcrespo.onewallet.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SplashViewModel(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

    fun handleIntent(intent: SplashIntent) {
        when (intent) {
            is SplashIntent.IsOnboardingCompleted -> isOnboardingCompleted()
        }
    }

    private fun isOnboardingCompleted() {
        val isOnboardingCompleted = onboardingRepository.isOnboardingCompleted()
        _uiState.update {
            it.copy(
                onboardingCompleted = isOnboardingCompleted
            )
        }
    }
}