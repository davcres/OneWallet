package com.davidcrespo.onewallet.domain.repository

interface OnboardingRepository {
    fun isOnboardingCompleted(): Boolean
    fun setOnboardingCompleted(completed: Boolean)

    fun isPortfolioOnboardingCompleted(): Boolean
    fun setPortfolioOnboardingCompleted(completed: Boolean)
}
