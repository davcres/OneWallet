package com.davidcrespo.onewallet.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.repository.OnboardingRepository
import com.davidcrespo.onewallet.domain.usecase.portfolio.ClearPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.SeedInitialPortfolioUseCase
import kotlinx.coroutines.launch

class PortfolioOnboardingViewModel(
    private val onboardingRepository: OnboardingRepository,
    private val seedInitialPortfolioUseCase: SeedInitialPortfolioUseCase,
    private val clearPortfolioUseCase: ClearPortfolioUseCase
) : ViewModel() {

    fun handleIntent(intent: PortfolioOnboardingIntent) {
        when (intent) {
            PortfolioOnboardingIntent.StartTutorial -> startTutorial()
            PortfolioOnboardingIntent.SkipTutorial -> skipTutorial()
        }
    }

    private fun startTutorial() {
        viewModelScope.launch {
            seedInitialPortfolioUseCase.invoke(Currency(EUR))
        }
    }

    private fun skipTutorial() {
        viewModelScope.launch {
            clearPortfolioUseCase()
            onboardingRepository.setPortfolioOnboardingCompleted(true)
        }
    }
}
