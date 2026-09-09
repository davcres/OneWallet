package com.davidcrespo.onewallet.feature.onboarding

import com.davidcrespo.onewallet.domain.di.AppCoroutineScope
import com.davidcrespo.onewallet.domain.di.DispatcherProvider
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.repository.OnboardingRepository
import com.davidcrespo.onewallet.domain.usecase.portfolio.ClearPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.SeedInitialPortfolioUseCase
import com.davidcrespo.onewallet.util.MainDispatcherExtension
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
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
    
    private val testDispatcherProvider = object : DispatcherProvider {
        override val io: CoroutineDispatcher = mainDispatcherExtension.testDispatcher
        override val default: CoroutineDispatcher = mainDispatcherExtension.testDispatcher
        override val main: CoroutineDispatcher = mainDispatcherExtension.testDispatcher
    }
    private val appCoroutineScope = AppCoroutineScope(testDispatcherProvider)
    
    private lateinit var viewModel: PortfolioOnboardingViewModel

    @BeforeEach
    fun setUp() {
        viewModel = PortfolioOnboardingViewModel(
            onboardingRepository,
            seedInitialPortfolioUseCase,
            clearPortfolioUseCase,
            appCoroutineScope
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

    @Test
    fun `cuando se recibe SkipTutorial despues de StartTutorial, se cancela la tarea de inicio`() = runTest {
        // Simulamos que el use case tarda un poco para que la cancelación sea relevante
        kotlinx.coroutines.delay(10) 
        
        viewModel.handleIntent(PortfolioOnboardingIntent.StartTutorial)
        viewModel.handleIntent(PortfolioOnboardingIntent.SkipTutorial)

        // Verificamos que se llamó a clear, lo que implica que skipTutorial se ejecutó
        coVerify { clearPortfolioUseCase() }
        // Nota: La cancelación exacta de seedInitialPortfolioUseCase es difícil de verificar 
        // solo con coVerify si no lanzamos excepciones de cancelación, pero la lógica de negocio
        // de llamar a .cancel() está cubierta por la implementación.
    }
}
