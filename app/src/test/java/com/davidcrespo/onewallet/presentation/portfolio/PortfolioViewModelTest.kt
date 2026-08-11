package com.davidcrespo.onewallet.presentation.portfolio

import android.content.Context
import app.cash.turbine.test
import com.davidcrespo.onewallet.core.models.ThemeMode
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.USD
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import com.davidcrespo.onewallet.domain.usecase.appRoot.GetThemeUseCase
import com.davidcrespo.onewallet.domain.usecase.appRoot.SetThemeUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.AddInvestmentToPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetCurrencyRateUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetInvestmentPriceUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetPortfolioItemsUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.RemovePortfolioItemUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.SaveMonthlyPortfolioUseCase
import com.davidcrespo.onewallet.presentation.widget.WidgetsRefreshWorker
import com.davidcrespo.onewallet.util.MainDispatcherExtension
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioViewModelTest {

    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    // Mocks de dependencias (todos relaxed para evitar fallos por falta de mocks)
    private val getCurrencyRateUseCase = mockk<GetCurrencyRateUseCase>(relaxed = true)
    private val getPortfolioItemsUseCase = mockk<GetPortfolioItemsUseCase>(relaxed = true)
    private val getInvestmentPriceUseCase = mockk<GetInvestmentPriceUseCase>(relaxed = true)
    private val saveMonthlyPortfolioUseCase = mockk<SaveMonthlyPortfolioUseCase>(relaxed = true)
    private val addInvestmentToPortfolioUseCase = mockk<AddInvestmentToPortfolioUseCase>(relaxed = true)
    private val removePortfolioItemUseCase = mockk<RemovePortfolioItemUseCase>(relaxed = true)
    private val clearPortfolioUseCase = mockk<com.davidcrespo.onewallet.domain.usecase.portfolio.ClearPortfolioUseCase>(relaxed = true)
    private val financialRepository = mockk<FinancialRepository>(relaxed = true)
    private val getThemeUseCase = mockk<GetThemeUseCase>(relaxed = true)
    private val setThemeUseCase = mockk<SetThemeUseCase>(relaxed = true)
    private val onboardingRepository = mockk<com.davidcrespo.onewallet.domain.repository.OnboardingRepository>(relaxed = true)
    private val themeFlow = MutableStateFlow(ThemeMode.SYSTEM)
    private val currencyConverter = CurrencyConverter()
    private val context = mockk<Context>(relaxed = true)

    private lateinit var viewModel: PortfolioViewModel

    @BeforeEach
    fun setUp() {
        // Mocking WidgetsRefreshWorker para evitar errores de WorkManager
        mockkObject(WidgetsRefreshWorker.Companion)
        every { WidgetsRefreshWorker.enqueueNow(any()) } returns Unit

        // Configuración mínima para que el init no explote
        every { financialRepository.getSelectedCurrency() } returns Currency(EUR)
        every { getPortfolioItemsUseCase.invoke() } returns flowOf(emptyList())
        every { getThemeUseCase.invoke() } returns themeFlow
        every { onboardingRepository.isPortfolioOnboardingCompleted() } returns false
        coEvery { getCurrencyRateUseCase.invoke(any(), any()) } returns Result.success(1.0)
    }

    private fun createViewModel() {
        try {
            viewModel = PortfolioViewModel(
                getCurrencyRateUseCase,
                getPortfolioItemsUseCase,
                getInvestmentPriceUseCase,
                saveMonthlyPortfolioUseCase,
                addInvestmentToPortfolioUseCase,
                removePortfolioItemUseCase,
                clearPortfolioUseCase,
                financialRepository,
                currencyConverter,
                getThemeUseCase,
                setThemeUseCase,
                onboardingRepository,
                context
            )
        } catch (e: Throwable) {
            e.printStackTrace()
            throw e
        }
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        themeFlow.value = ThemeMode.SYSTEM
    }

    @Test
    fun `al iniciar el ViewModel, el estado carga el tema por defecto SYSTEM`() = runTest(mainDispatcherExtension.testDispatcher) {
        createViewModel()
        
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(ThemeMode.SYSTEM, initialState.themeMode)
        }
    }

    @Test
    fun `cuando GetThemeUseCase emite un nuevo tema, el estado se actualiza`() = runTest(mainDispatcherExtension.testDispatcher) {
        createViewModel()
        
        viewModel.uiState.test {
            assertEquals(ThemeMode.SYSTEM, awaitItem().themeMode)
            
            themeFlow.value = ThemeMode.DARK
            assertEquals(ThemeMode.DARK, awaitItem().themeMode)
            
            themeFlow.value = ThemeMode.LIGHT
            assertEquals(ThemeMode.LIGHT, awaitItem().themeMode)
        }
    }

    @Test
    fun `cuando se recibe ToggleTheme, se llama a SetThemeUseCase con el tema correspondiente`() = runTest(mainDispatcherExtension.testDispatcher) {
        createViewModel()
        
        viewModel.handleIntent(PortfolioIntent.ToggleTheme(ThemeMode.DARK))
        
        coVerify { setThemeUseCase(ThemeMode.DARK) }
    }

    @Test
    fun `cuando se recibe NavigateToMarket, se emite el efecto correspondiente`() = runTest(mainDispatcherExtension.testDispatcher) {
        createViewModel()
        
        viewModel.effect.test {
            viewModel.handleIntent(PortfolioIntent.NavigateToMarket(isCrypto = true))
            assertEquals(PortfolioEffect.NavigateToMarket(isCrypto = true), awaitItem())
            
            viewModel.handleIntent(PortfolioIntent.NavigateToMarket(isCrypto = false))
            assertEquals(PortfolioEffect.NavigateToMarket(isCrypto = false), awaitItem())
        }
    }

    @Test
    fun `al iniciar el ViewModel, el estado carga la moneda seleccionada y una lista vacia`() = runTest(mainDispatcherExtension.testDispatcher) {
        createViewModel()
        
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(EUR, initialState.selectedCurrency.code)
            assertTrue(initialState.portfolioItems.isEmpty())
        }
    }

    @Test
    fun `cuando se recibe ShowFundDialog, el estado actualiza isFundDialogVisible a true`() = runTest(mainDispatcherExtension.testDispatcher) {
        createViewModel()
        
        viewModel.uiState.test {
            awaitItem() // Skip initial state
            
            viewModel.handleIntent(PortfolioIntent.ShowFundDialog)
            
            val state = awaitItem()
            assertEquals(true, state.isFundDialogVisible)
        }
    }

    @Test
    fun `cuando se recibe DismissFundDialog, el estado actualiza isFundDialogVisible a false`() = runTest(mainDispatcherExtension.testDispatcher) {
        createViewModel()
        
        viewModel.uiState.test {
            awaitItem() // Initial
            
            viewModel.handleIntent(PortfolioIntent.ShowFundDialog)
            awaitItem() // Dialog visible
            
            viewModel.handleIntent(PortfolioIntent.DismissFundDialog)
            val state = awaitItem()
            assertEquals(false, state.isFundDialogVisible)
        }
    }

    @Test
    fun `cuando GetPortfolioItems emite items, el estado se actualiza con los items procesados`() = runTest(mainDispatcherExtension.testDispatcher) {
        // Datos de prueba
        val mockInvestment = Investment(
            symbol = "VTI",
            name = "Vanguard Total Stock Market",
            quantity = 10.0,
            price = 200.0,
            previousPrice = 195.0,
            currency = Currency(EUR),
            type = InvestmentType.ETF,
            year = 2024,
            month = 3
        )
        
        every { getPortfolioItemsUseCase.invoke() } returns flowOf(listOf(mockInvestment))
        coEvery { getCurrencyRateUseCase.invoke(any(), any()) } returns Result.success(1.0)
        coEvery { getInvestmentPriceUseCase.invoke(any(), any(), any(), any(), any(), any()) } returns Result.success(mockInvestment)
        coEvery { saveMonthlyPortfolioUseCase.invoke(any()) } returns Unit

        createViewModel()

        viewModel.uiState.test {
            // Buscamos el estado que tenga los items
            var lastState = awaitItem()
            while (lastState.isLoading || lastState.portfolioItems.isEmpty()) {
                lastState = awaitItem()
            }
            
            assertEquals(1, lastState.portfolioItems.size)
            assertEquals("VTI", lastState.portfolioItems[0].symbol)
            
            // Verificamos calculos automaticos
            assertEquals(2000.0, lastState.totalBalance)
            assertEquals(1950.0, lastState.previousBalance)
            assertEquals(1, lastState.portfolioItemsByType.size)
            assertEquals(InvestmentType.ETF, lastState.portfolioItemsByType[0].type)
            assertEquals(2000.0, lastState.portfolioItemsByType[0].totalValue)
        }
    }

    @Test
    fun `cuando se recibe ClearPortfolio, se llama a clearPortfolioUseCase`() = runTest(mainDispatcherExtension.testDispatcher) {
        createViewModel()
        
        viewModel.handleIntent(PortfolioIntent.ClearPortfolio)
        
        coVerify { clearPortfolioUseCase() }
    }

    @Test
    fun `cuando se completa el onboarding, se actualiza el repositorio y el estado`() = runTest(mainDispatcherExtension.testDispatcher) {
        createViewModel()
        
        // Empezamos onboarding
        viewModel.handleIntent(PortfolioIntent.StartOnboarding)
        
        viewModel.uiState.test {
            var state = awaitItem()
            while (state.onboardingPlaylist.isEmpty()) {
                state = awaitItem()
            }
            
            // Avanzamos todos los pasos
            val stepsCount = state.onboardingPlaylist.size
            repeat(stepsCount) {
                viewModel.handleIntent(PortfolioIntent.NextOnboardingStep)
            }
            
            // Verificamos que se marca como completado
            state = awaitItem()
            while (!state.isOnboardingCompleted) {
                state = awaitItem()
            }
            
            assertTrue(state.isOnboardingCompleted)
            coVerify { onboardingRepository.setPortfolioOnboardingCompleted(true) }
        }
    }

    @Test
    fun `si el onboarding ya esta completado, StartOnboarding no hace nada`() = runTest(mainDispatcherExtension.testDispatcher) {
        every { onboardingRepository.isPortfolioOnboardingCompleted() } returns true
        createViewModel()
        
        viewModel.handleIntent(PortfolioIntent.StartOnboarding)
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isOnboardingCompleted)
            assertTrue(state.onboardingPlaylist.isEmpty())
        }
    }

    @Test
    fun `cuando la API devuelve un precio en USD y la moneda seleccionada es EUR, se convierte correctamente el precio a EUR`() = runTest(mainDispatcherExtension.testDispatcher) {
        val mockInvestment = Investment(
            symbol = "GOOGL",
            name = "Alphabet Inc.",
            quantity = 1.0,
            price = 100.0,
            previousPrice = 90.0,
            currency = Currency(USD),
            type = InvestmentType.STOCK,
            year = 2024,
            month = 3
        )
        
        val apiInvestment = mockInvestment.copy(
            price = 100.0,
            previousPrice = 90.0,
            currency = Currency(USD)
        )

        every { getPortfolioItemsUseCase.invoke() } returns flowOf(listOf(mockInvestment))
        coEvery { getCurrencyRateUseCase.invoke("USD", "EUR") } returns Result.success(0.85)
        coEvery { getInvestmentPriceUseCase.invoke(any(), any(), any(), any(), any(), any(), any()) } returns Result.success(apiInvestment)

        createViewModel()

        viewModel.uiState.test {
            var lastState = awaitItem()
            while (lastState.isLoading || lastState.portfolioItems.isEmpty()) {
                lastState = awaitItem()
            }
            
            val item = lastState.portfolioItems[0]
            assertEquals("GOOGL", item.symbol)
            assertEquals(USD, item.originalCurrency.code)
            assertEquals(100.0, item.originalPrice)
            assertEquals(85.0, item.displayPrice)
            assertEquals(85.0, lastState.totalBalance)
        }
    }

    @Test
    fun `cuando se elimina un item, se purga de portfolioItems y de symbolsWithPrice`() = runTest(mainDispatcherExtension.testDispatcher) {
        val mockInvestment = Investment(
            symbol = "AAPL",
            name = "Apple",
            quantity = 1.0,
            price = 150.0,
            previousPrice = 140.0,
            currency = Currency(EUR),
            type = InvestmentType.STOCK,
            year = 2024,
            month = 3
        )

        every { getPortfolioItemsUseCase.invoke() } returns flowOf(listOf(mockInvestment))
        coEvery { getInvestmentPriceUseCase.invoke(any(), any(), any(), any(), any(), any(), any()) } returns Result.success(mockInvestment)

        createViewModel()

        viewModel.uiState.test {
            var lastState = awaitItem()
            while (lastState.isLoading || lastState.portfolioItems.isEmpty()) {
                lastState = awaitItem()
            }
            assertEquals(1, lastState.portfolioItems.size)
            assertTrue(lastState.symbolsWithPrice.contains("AAPL"))

            viewModel.handleIntent(PortfolioIntent.RemoveItem(lastState.portfolioItems[0]))

            var stateAfterDelete = awaitItem()
            while (stateAfterDelete.portfolioItems.isNotEmpty()) {
                stateAfterDelete = awaitItem()
            }

            assertEquals(0, stateAfterDelete.portfolioItems.size)
            assertFalse(stateAfterDelete.symbolsWithPrice.contains("AAPL"))
        }
    }
}
