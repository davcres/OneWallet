package com.davidcrespo.onewallet.presentation.onboarding

import com.davidcrespo.onewallet.domain.repository.OnboardingRepository
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OnboardingViewModelTest {

    private val onboardingRepository = mockk<OnboardingRepository>(relaxed = true)
    private lateinit var viewModel: OnboardingViewModel

    @BeforeEach
    fun setUp() {
        viewModel = OnboardingViewModel(onboardingRepository)
    }

    @Test
    fun `cuando se recibe SetOnboardingCompleted, se marca como completado en el repositorio`() {
        // Configuracion (opcional si es relaxed, pero mejor ser explicito)
        every { onboardingRepository.setOnboardingCompleted(any()) } returns Unit
        
        // Acción
        viewModel.handleIntent(OnboardingIntent.SetOnboardingCompleted)
        
        // Verificación
        verify(exactly = 1) { onboardingRepository.setOnboardingCompleted(true) }
        confirmVerified(onboardingRepository)
    }
}
