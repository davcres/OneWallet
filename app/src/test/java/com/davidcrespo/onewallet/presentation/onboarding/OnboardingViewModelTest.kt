package com.davidcrespo.onewallet.presentation.onboarding

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.repository.OnboardingRepository
import com.davidcrespo.onewallet.domain.usecase.portfolio.SeedInitialPortfolioUseCase
import com.davidcrespo.onewallet.util.MainDispatcherExtension
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private val onboardingRepository = mockk<OnboardingRepository>(relaxed = true)
    private val seedInitialPortfolioUseCase = mockk<SeedInitialPortfolioUseCase>(relaxed = true)
    private lateinit var viewModel: OnboardingViewModel

    @BeforeEach
    fun setUp() {
        viewModel = OnboardingViewModel(onboardingRepository, seedInitialPortfolioUseCase)
    }

    @Test
    fun `cuando se recibe SetOnboardingCompleted, se marca como completado en el repositorio`() {
        // Acción
        viewModel.handleIntent(OnboardingIntent.SetOnboardingCompleted)
        
        // Verificación
        verify(exactly = 1) { onboardingRepository.setOnboardingCompleted(true) }
        confirmVerified(onboardingRepository)
    }

    @Test
    fun `cuando se recibe SeedInitialPortfolio, se ejecuta el caso de uso correspondiente`() = runTest {
        // Acción
        viewModel.handleIntent(OnboardingIntent.SeedInitialPortfolio)

        // Verificación
        coVerify(exactly = 1) { seedInitialPortfolioUseCase.invoke(Currency(EUR)) }
    }
}
