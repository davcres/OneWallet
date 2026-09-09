package com.davidcrespo.onewallet.data.repository

import com.davidcrespo.onewallet.domain.repository.OnboardingRepository
import com.russhwolf.settings.Settings

class OnboardingRepositoryImpl(
    private val settings: Settings,
) : OnboardingRepository {

    override fun isOnboardingCompleted(): Boolean {
        return settings.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    override fun setOnboardingCompleted(completed: Boolean) {
        settings.putBoolean(KEY_ONBOARDING_COMPLETED, completed)
    }

    override fun isPortfolioOnboardingCompleted(): Boolean {
        return settings.getBoolean(KEY_ONBOARDING_IN_APP_COMPLETED, false)
    }

    override fun setPortfolioOnboardingCompleted(completed: Boolean) {
        settings.putBoolean(KEY_ONBOARDING_IN_APP_COMPLETED, completed)
    }

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_ONBOARDING_IN_APP_COMPLETED = "onboarding_in_app_completed"
    }
}
