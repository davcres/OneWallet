package com.davidcrespo.onewallet.presentation.onboarding

import androidx.lifecycle.ViewModel
import com.davidcrespo.onewallet.domain.repository.OnboardingRepository

class OnboardingViewModel(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    fun handleIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.SetOnboardingCompleted -> setOnboardingCompleted()
        }
    }

    private fun setOnboardingCompleted() {
        onboardingRepository.setOnboardingCompleted(true)
    }
}
