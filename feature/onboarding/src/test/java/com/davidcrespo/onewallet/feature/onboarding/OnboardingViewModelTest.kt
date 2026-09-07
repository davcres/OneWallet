package com.davidcrespo.onewallet.feature.onboarding

import com.davidcrespo.onewallet.domain.repository.OnboardingRepository
import com.davidcrespo.onewallet.util.MainDispatcherExtension
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private val onboardingRepository = mockk<OnboardingRepository>(relaxed = true)
    private lateinit var viewModel: OnboardingViewModel

    @BeforeEach
    fun setUp() {
        viewModel = OnboardingViewModel(onboardingRepository)
    }

    @Test
    fun `cuando se recibe SetOnboardingCompleted, se marca como completado en el repositorio`() {
        // Acción
        viewModel.handleIntent(OnboardingIntent.SetOnboardingCompleted)
        
        // Verificación
        verify(exactly = 1) { onboardingRepository.setOnboardingCompleted(true) }
        confirmVerified(onboardingRepository)
    }
}
