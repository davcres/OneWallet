package com.davidcrespo.onewallet.presentation.portfolio

import android.content.Context
import androidx.work.WorkManager
import app.cash.turbine.test
import com.davidcrespo.onewallet.core.models.ThemeMode
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
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
    private val financialRepository = mockk<FinancialRepository>(relaxed = true)
    private val getThemeUseCase = mockk<GetThemeUseCase>(relaxed = true)
    private val setThemeUseCase = mockk<SetThemeUseCase>(relaxed = true)
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
                financialRepository,
                currencyConverter,
                getThemeUseCase,
                setThemeUseCase,
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
    fun `cuando se recibe ChangeCurrency, se actualiza la moneda y se recalculan balances`() = runTest(mainDispatcherExtension.testDispatcher) {
        val mockInvestment = Investment(
            symbol = "VTI",
            name = "Vanguard Total Stock Market",
            quantity = 10.0,
            price = 100.0,
            previousPrice = 90.0,
            currency = Currency(EUR),
            type = InvestmentType.ETF,
            year = 2024,
            month = 3
        )
        
        every { getPortfolioItemsUseCase.invoke() } returns flowOf(listOf(mockInvestment))
        every { financialRepository.getSelectedCurrency() } returns Currency(EUR)
        // EUR to USD rate = 1.1
        coEvery { getCurrencyRateUseCase.invoke("EUR", "USD") } returns Result.success(1.1)
        coEvery { getInvestmentPriceUseCase.invoke(any(), any(), any(), any(), any(), any()) } returns Result.success(mockInvestment)

        createViewModel()

        viewModel.uiState.test {
            var state = awaitItem()
            while (state.portfolioItems.isEmpty()) {
                state = awaitItem()
            }
            
            assertEquals(EUR, state.selectedCurrency.code)
            assertEquals(1000.0, state.totalBalance)

            viewModel.handleIntent(PortfolioIntent.ChangeCurrency)
            
            state = awaitItem()
            while (state.selectedCurrency.code != com.davidcrespo.onewallet.domain.model.investment.USD) {
                state = awaitItem()
            }
            
            assertEquals(com.davidcrespo.onewallet.domain.model.investment.USD, state.selectedCurrency.code)
            assertEquals(1100.0, state.totalBalance, 0.1)
            assertEquals(990.0, state.previousBalance, 0.1)
            assertEquals(1100.0, state.portfolioItemsByType[0].totalValue, 0.1)
        }
    }
}
