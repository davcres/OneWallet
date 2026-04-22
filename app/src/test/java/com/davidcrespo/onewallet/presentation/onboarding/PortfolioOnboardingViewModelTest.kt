package com.davidcrespo.onewallet.presentation.onboarding

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.repository.OnboardingRepository
import com.davidcrespo.onewallet.domain.usecase.portfolio.ClearPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.SeedInitialPortfolioUseCase
import com.davidcrespo.onewallet.util.MainDispatcherExtension
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioOnboardingViewModelTest {

    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private val onboardingRepository = mockk<OnboardingRepository>(relaxed = true)
    private val seedInitialPortfolioUseCase = mockk<SeedInitialPortfolioUseCase>(relaxed = true)
    private val clearPortfolioUseCase = mockk<ClearPortfolioUseCase>(relaxed = true)
    private lateinit var viewModel: PortfolioOnboardingViewModel

    @BeforeEach
    fun setUp() {
        viewModel = PortfolioOnboardingViewModel(
            onboardingRepository,
            seedInitialPortfolioUseCase,
            clearPortfolioUseCase
        )
    }

    @Test
    fun `cuando se recibe StartTutorial, se ejecuta SeedInitialPortfolioUseCase`() = runTest {
        viewModel.handleIntent(PortfolioOnboardingIntent.StartTutorial)

        coVerify { seedInitialPortfolioUseCase(Currency(EUR)) }
    }

    @Test
    fun `cuando se recibe SkipTutorial, se borra la cartera y se marca como completado`() = runTest {
        viewModel.handleIntent(PortfolioOnboardingIntent.SkipTutorial)

        coVerify { clearPortfolioUseCase() }
        verify { onboardingRepository.setPortfolioOnboardingCompleted(true) }
    }
}
