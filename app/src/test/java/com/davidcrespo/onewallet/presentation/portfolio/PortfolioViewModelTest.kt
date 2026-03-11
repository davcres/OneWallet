package com.davidcrespo.onewallet.presentation.portfolio

import android.content.Context
import androidx.work.WorkManager
import app.cash.turbine.test
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import com.davidcrespo.onewallet.domain.usecase.portfolio.AddInvestmentToPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetCurrencyRateUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetInvestmentPriceUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetPortfolioItemsUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.RemovePortfolioItemUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.SaveMonthlyPortfolioUseCase
import com.davidcrespo.onewallet.presentation.widget.WidgetsRefreshWorker
import com.davidcrespo.onewallet.util.MainDispatcherRule
import com.davidcrespo.onewallet.util.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Mocks de dependencias (todos relaxed para evitar fallos por falta de mocks)
    private val getCurrencyRateUseCase = mockk<GetCurrencyRateUseCase>(relaxed = true)
    private val getPortfolioItemsUseCase = mockk<GetPortfolioItemsUseCase>(relaxed = true)
    private val getInvestmentPriceUseCase = mockk<GetInvestmentPriceUseCase>(relaxed = true)
    private val saveMonthlyPortfolioUseCase = mockk<SaveMonthlyPortfolioUseCase>(relaxed = true)
    private val addInvestmentToPortfolioUseCase = mockk<AddInvestmentToPortfolioUseCase>(relaxed = true)
    private val removePortfolioItemUseCase = mockk<RemovePortfolioItemUseCase>(relaxed = true)
    private val financialRepository = mockk<FinancialRepository>(relaxed = true)
    private val currencyConverter = CurrencyConverter()
    private val context = mockk<Context>(relaxed = true)

    private lateinit var viewModel: PortfolioViewModel

    @Before
    fun setUp() {
        // Mocking WidgetsRefreshWorker para evitar errores de WorkManager
        mockkObject(WidgetsRefreshWorker.Companion)
        every { WidgetsRefreshWorker.enqueueNow(any()) } returns Unit

        // Configuración mínima para que el init no explote
        every { financialRepository.getSelectedCurrency() } returns Currency(EUR)
        every { getPortfolioItemsUseCase.invoke() } returns flowOf(emptyList())
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
                context
            )
        } catch (e: Throwable) {
            e.printStackTrace()
            throw e
        }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `al iniciar el ViewModel, el estado carga la moneda seleccionada y una lista vacia`() = runTest {
        createViewModel()
        
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(EUR, initialState.selectedCurrency.code)
            assertTrue(initialState.portfolioItems.isEmpty())
        }
    }

    @Test
    fun `cuando se recibe ShowFundDialog, el estado actualiza isFundDialogVisible a true`() = runTest {
        createViewModel()
        
        viewModel.uiState.test {
            awaitItem() // Skip initial state
            
            viewModel.handleIntent(PortfolioIntent.ShowFundDialog)
            
            val state = awaitItem()
            assertEquals(true, state.isFundDialogVisible)
        }
    }

    @Test
    fun `cuando se recibe DismissFundDialog, el estado actualiza isFundDialogVisible a false`() = runTest {
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
    fun `cuando GetPortfolioItems emite items, el estado se actualiza con los items procesados`() = runTest {
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
        }
    }
}
