package com.davidcrespo.onewallet.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.repository.OnboardingRepository
import com.davidcrespo.onewallet.domain.usecase.portfolio.SeedInitialPortfolioUseCase
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val onboardingRepository: OnboardingRepository,
    private val seedInitialPortfolioUseCase: SeedInitialPortfolioUseCase,
) : ViewModel() {

    fun handleIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.SeedInitialPortfolio -> seedInitialPortfolio()
            is OnboardingIntent.SetOnboardingCompleted -> setOnboardingCompleted()
        }
    }

    private fun seedInitialPortfolio() {
        viewModelScope.launch {
            seedInitialPortfolioUseCase.invoke(Currency(EUR))
        }
    }

    private fun setOnboardingCompleted() {
        onboardingRepository.setOnboardingCompleted(true)
    }
}