package com.davidcrespo.onewallet.data.repository

import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OnboardingRepositoryImplTest {

    private val sharedPreferences = mockk<SharedPreferences>(relaxed = true)
    private val editor = mockk<SharedPreferences.Editor>(relaxed = true)
    private lateinit var repository: OnboardingRepositoryImpl

    @BeforeEach
    fun setUp() {
        // Configuramos el mock para que devuelva el editor cuando se llame a edit()
        every { sharedPreferences.edit() } returns editor
        // El editor suele devolver una referencia a sí mismo en los métodos put
        every { editor.putBoolean(any(), any()) } returns editor
        
        repository = OnboardingRepositoryImpl(sharedPreferences)
    }

    @Test
    fun `cuando se consulta si el onboarding ha terminado, lee de las SharedPreferences`() {
        // Given
        every { sharedPreferences.getBoolean("onboarding_completed", false) } returns true

        // When
        val result = repository.isOnboardingCompleted()

        // Then
        assertEquals(true, result)
        verify { sharedPreferences.getBoolean("onboarding_completed", false) }
    }

    @Test
    fun `cuando se marca el onboarding como completado, guarda el valor en SharedPreferences`() {
        // When
        repository.setOnboardingCompleted(true)

        // Then
        verify { editor.putBoolean("onboarding_completed", true) }
        verify { editor.apply() } // edit {} de KTX llama a apply() por defecto
    }
}
