package com.davidcrespo.onewallet.presentation.splash

import app.cash.turbine.test
import com.davidcrespo.onewallet.domain.di.AppCoroutineScope
import com.davidcrespo.onewallet.domain.repository.OnboardingRepository
import com.davidcrespo.onewallet.domain.usecase.market.GetUSMarketAssetsUseCase
import com.davidcrespo.onewallet.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val appCoroutineScope = mockk<AppCoroutineScope>(relaxed = true)
    private val onboardingRepository = mockk<OnboardingRepository>(relaxed = true)
    private val getMarketAssetsUseCase = mockk<GetUSMarketAssetsUseCase>(relaxed = true)

    private lateinit var viewModel: SplashViewModel

    @Before
    fun setUp() {
        // El scope interno del AppCoroutineScope debe usar nuestro dispatcher de test
        every { appCoroutineScope.scope } returns CoroutineScope(mainDispatcherRule.testDispatcher)
        
        viewModel = SplashViewModel(
            appCoroutineScope,
            onboardingRepository,
            getMarketAssetsUseCase
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `cuando se recibe IsOnboardingCompleted, actualiza el estado con el valor del repositorio`() = runTest {
        every { onboardingRepository.isOnboardingCompleted() } returns true
        
        viewModel.uiState.test {
            // El estado inicial es null
            assertEquals(null, awaitItem().onboardingCompleted)
            
            viewModel.handleIntent(SplashIntent.IsOnboardingCompleted)
            
            assertEquals(true, awaitItem().onboardingCompleted)
        }
    }

    @Test
    fun `cuando se recibe LoadMarkets, se llama al UseCase para precargar stocks y crypto`() = runTest {
        viewModel.handleIntent(SplashIntent.LoadMarkets)
        
        // Verificamos que se llame con false (stocks) y true (crypto)
        coVerify(exactly = 1) { getMarketAssetsUseCase(false) }
        coVerify(exactly = 1) { getMarketAssetsUseCase(true) }
    }
}
