package com.davidcrespo.onewallet.presentation.historical

import app.cash.turbine.test
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import com.davidcrespo.onewallet.domain.usecase.historical.GetMonthlyHistoryUseCase
import com.davidcrespo.onewallet.domain.usecase.portfolio.GetCurrencyRateUseCase
import com.davidcrespo.onewallet.presentation.models.toUI
import com.davidcrespo.onewallet.presentation.portfolio.CurrencyConverter
import com.davidcrespo.onewallet.util.MainDispatcherExtension
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class HistoricalViewModelTest {

    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private val getMonthlyHistoryUseCase = mockk<GetMonthlyHistoryUseCase>(relaxed = true)
    private val financialRepository = mockk<FinancialRepository>(relaxed = true)
    private val getCurrencyRateUseCase = mockk<GetCurrencyRateUseCase>(relaxed = true)
    private val currencyConverter = CurrencyConverter()

    private lateinit var viewModel: HistoricalViewModel

    private fun createViewModel() {
        viewModel = HistoricalViewModel(
            getMonthlyHistoryUseCase,
            financialRepository,
            getCurrencyRateUseCase,
            currencyConverter
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `al cargar datos iniciales, el estado agrupa las inversiones por mes`() = runTest(mainDispatcherExtension.testDispatcher) {
        val asset1 = Investment("AAPL", "Apple", 10.0, 150.0, 0.0, Currency(EUR), InvestmentType.STOCK, 2024, 3)
        val asset2 = Investment("MSFT", "Microsoft", 5.0, 300.0, 0.0, Currency(EUR), InvestmentType.STOCK, 2024, 2)
        
        coEvery { getMonthlyHistoryUseCase() } returns Result.success(listOf(asset1, asset2))
        every { financialRepository.getSelectedCurrency() } returns Currency(EUR)
        coEvery { getCurrencyRateUseCase(any(), any()) } returns Result.success(1.0)
        
        createViewModel()
        viewModel.handleIntent(HistoricalIntent.LoadInitialData)
        
        viewModel.uiState.test {
            // Buscamos un estado que no este cargando y tenga historia
            var state = awaitItem()
            while (state.isLoading || state.history.isEmpty()) {
                state = awaitItem()
            }
            
            assertFalse(state.isLoading)
            assertEquals(2, state.history.size)
        }
    }

    @Test
    fun `cuando se selecciona un mes, se extrae el detalle y el mes previo`() = runTest(mainDispatcherExtension.testDispatcher) {
        val marchAsset = Investment("AAPL", "Apple", 10.0, 150.0, 0.0, Currency(EUR), InvestmentType.STOCK, 2024, 3)
        val febAsset = Investment("MSFT", "Microsoft", 5.0, 300.0, 0.0, Currency(EUR), InvestmentType.STOCK, 2024, 2)
        
        coEvery { getMonthlyHistoryUseCase() } returns Result.success(listOf(marchAsset, febAsset))
        every { financialRepository.getSelectedCurrency() } returns Currency(EUR)
        coEvery { getCurrencyRateUseCase(any(), any()) } returns Result.success(1.0)

        createViewModel()
        viewModel.handleIntent(HistoricalIntent.LoadInitialData)
        
        viewModel.uiState.test {
            // Esperamos a que cargue
            var state = awaitItem()
            while (state.history.isEmpty()) {
                state = awaitItem()
            }
            
            viewModel.handleIntent(HistoricalIntent.SelectMonth(2024, 3))
            
            // Buscamos el estado con la seleccion
            state = awaitItem()
            while (state.selectedMonthDetail == null) {
                state = awaitItem()
            }
            
            assertEquals(1, state.selectedMonthDetail?.size)
            assertEquals("AAPL", state.selectedMonthDetail?.get(0)?.symbol)
            
            // El mes previo es Febrero
            assertNotNull(state.selectedPreviousMonth)
            assertEquals("MSFT", state.selectedPreviousMonth?.get(0)?.symbol)
        }
    }

    @Test
    fun `test completo de seleccion de inversion con comparativa de mes previo`() = runTest(mainDispatcherExtension.testDispatcher) {
        val marchAsset = Investment("AAPL", "Apple", 10.0, 150.0, 0.0, Currency(EUR), InvestmentType.STOCK, 2024, 3)
        val febAsset = Investment("AAPL", "Apple", 10.0, 140.0, 0.0, Currency(EUR), InvestmentType.STOCK, 2024, 2)
        
        coEvery { getMonthlyHistoryUseCase() } returns Result.success(listOf(marchAsset, febAsset))
        every { financialRepository.getSelectedCurrency() } returns Currency(EUR)
        coEvery { getCurrencyRateUseCase(any(), any()) } returns Result.success(1.0)

        createViewModel()
        viewModel.handleIntent(HistoricalIntent.LoadInitialData)
        
        viewModel.uiState.test {
            // Esperamos a que cargue
            var state = awaitItem()
            while (state.history.isEmpty()) {
                state = awaitItem()
            }
            
            viewModel.handleIntent(HistoricalIntent.SelectMonth(2024, 3))
            state = awaitItem()
            while (state.selectedMonthDetail == null) {
                state = awaitItem()
            }
            
            val marchAssetUI = state.selectedMonthDetail!![0]
            viewModel.handleIntent(HistoricalIntent.SelectInvestment(marchAssetUI))
            
            state = awaitItem()
            while (state.selectedInvestment == null) {
                state = awaitItem()
            }
            
            assertEquals("AAPL", state.selectedInvestment?.symbol)
            assertNotNull(state.selectedPreviousInvestment)
            assertEquals(140.0, state.selectedPreviousInvestment?.displayPrice ?: 0.0, 0.1)
        }
    }

    @Test
    fun `cuando se hace dismiss, se limpian las selecciones`() = runTest(mainDispatcherExtension.testDispatcher) {
        val asset = Investment("AAPL", "Apple", 10.0, 150.0, 0.0, Currency(EUR), InvestmentType.STOCK, 2024, 3)
        coEvery { getMonthlyHistoryUseCase() } returns Result.success(listOf(asset))
        every { financialRepository.getSelectedCurrency() } returns Currency(EUR)
        coEvery { getCurrencyRateUseCase(any(), any()) } returns Result.success(1.0)

        createViewModel()
        viewModel.handleIntent(HistoricalIntent.LoadInitialData)
        
        viewModel.uiState.test {
            // Esperamos a que cargue
            var state = awaitItem()
            while (state.history.isEmpty()) {
                state = awaitItem()
            }
            
            // Seleccionamos algo primero
            viewModel.handleIntent(HistoricalIntent.SelectMonth(2024, 3))
            state = awaitItem()
            while (state.selectedMonthDetail == null) {
                state = awaitItem()
            }
            
            // Ahora si hacemos dismiss
            viewModel.handleIntent(HistoricalIntent.DismissBottomSheet)
            state = awaitItem()
            assertNull(state.selectedMonthDetail)
            assertNull(state.selectedPreviousMonth)
            
            // Seleccionamos inversion
            val marchAssetUI = asset.toUI()
            viewModel.handleIntent(HistoricalIntent.SelectInvestment(marchAssetUI))
            state = awaitItem()
            while (state.selectedInvestment == null) {
                state = awaitItem()
            }
            
            viewModel.handleIntent(HistoricalIntent.DismissInvestmentDetail)
            state = awaitItem()
            assertNull(state.selectedInvestment)
        }
    }
}
