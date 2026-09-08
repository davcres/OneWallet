package com.davidcrespo.onewallet.data.repository

import com.russhwolf.settings.MapSettings
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OnboardingRepositoryImplTest {

    private lateinit var settings: MapSettings
    private lateinit var repository: OnboardingRepositoryImpl

    @BeforeEach
    fun setUp() {
        settings = MapSettings()
        repository = OnboardingRepositoryImpl(settings)
    }

    @Test
    fun `cuando se consulta si el onboarding ha terminado, lee de las Settings`() {
        // Given
        settings.putBoolean("onboarding_completed", true)

        // When
        val result = repository.isOnboardingCompleted()

        // Then
        assertTrue(result)
    }

    @Test
    fun `cuando se marca el onboarding como completado, guarda el valor en Settings`() {
        // Given
        assertFalse(repository.isOnboardingCompleted())

        // When
        repository.setOnboardingCompleted(true)

        // Then
        assertTrue(repository.isOnboardingCompleted())
    }

    @Test
    fun `cuando se consulta o actualiza el onboarding in-app, se gestiona correctamente`() {
        assertFalse(repository.isPortfolioOnboardingCompleted())

        repository.setPortfolioOnboardingCompleted(true)

        assertTrue(repository.isPortfolioOnboardingCompleted())
    }
}
