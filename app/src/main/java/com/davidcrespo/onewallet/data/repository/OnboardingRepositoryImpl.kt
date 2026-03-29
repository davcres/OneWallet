package com.davidcrespo.onewallet.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.davidcrespo.onewallet.domain.repository.OnboardingRepository

class OnboardingRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
) : OnboardingRepository {

    override fun isOnboardingCompleted(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    override fun setOnboardingCompleted(completed: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_ONBOARDING_COMPLETED, completed)
        }
    }

    override fun isPortfolioOnboardingCompleted(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_IN_APP_COMPLETED, false)
    }

    override fun setPortfolioOnboardingCompleted(completed: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_ONBOARDING_IN_APP_COMPLETED, completed)
        }
    }

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_ONBOARDING_IN_APP_COMPLETED = "onboarding_in_app_completed"
    }
}
