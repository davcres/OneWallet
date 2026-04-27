package com.davidcrespo.onewallet.presentation.onboarding

import androidx.lifecycle.ViewModel
import com.davidcrespo.onewallet.domain.di.AppCoroutineScope
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.repository.OnboardingRepository
import com.davidcrespo.onewallet.domain.usecase.portfolio.ClearPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.SeedInitialPortfolioUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PortfolioOnboardingViewModel(
    private val onboardingRepository: OnboardingRepository,
    private val seedInitialPortfolioUseCase: SeedInitialPortfolioUseCase,
    private val clearPortfolioUseCase: ClearPortfolioUseCase,
    private val applicationScope: AppCoroutineScope
) : ViewModel() {

    private var startTutorialJob: Job? = null

    fun handleIntent(intent: PortfolioOnboardingIntent) {
        when (intent) {
            PortfolioOnboardingIntent.StartTutorial -> startTutorial()
            PortfolioOnboardingIntent.SkipTutorial -> skipTutorial()
        }
    }

    private fun startTutorial() {
        startTutorialJob?.cancel()
        startTutorialJob = applicationScope.scope.launch {
            seedInitialPortfolioUseCase.invoke(Currency(EUR))
        }
    }

    private fun skipTutorial() {
        startTutorialJob?.cancel()
        applicationScope.scope.launch {
            clearPortfolioUseCase.invoke()
            onboardingRepository.setPortfolioOnboardingCompleted(true)
        }
    }
}
