package com.davidcrespo.onewallet.presentation.market.usMarket

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import com.davidcrespo.onewallet.domain.usecase.market.AddMarketAssetToPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.market.GetUSMarketAssetsUseCase
import com.davidcrespo.onewallet.presentation.models.toUI
import com.davidcrespo.onewallet.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UsMarketViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getUSMarketAssetsUseCase = mockk<GetUSMarketAssetsUseCase>(relaxed = true)
    private val addMarketAssetToPortfolioUseCase = mockk<AddMarketAssetToPortfolioUseCase>(relaxed = true)
    private val savedStateHandle = SavedStateHandle()

    private lateinit var viewModel: UsMarketViewModel

    @Before
    fun setUp() {
    }

    private fun createViewModel() {
        viewModel = UsMarketViewModel(
            savedStateHandle,
            getUSMarketAssetsUseCase,
            addMarketAssetToPortfolioUseCase
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `al iniciar el ViewModel, carga la query de busqueda desde el SavedStateHandle`() = runTest {
        savedStateHandle["searchQuery"] = "Apple"
        createViewModel()
        
        viewModel.uiState.test {
            assertEquals("Apple", awaitItem().searchQuery)
        }
    }

    @Test
    fun `cuando se recibe LoadInitialData, se actualiza el estado con los assets cargados`() = runTest {
        val asset = MarketAsset(
            symbol = "AAPL",
            price = 150.0,
            currency = Currency(EUR),
            type = InvestmentType.STOCK,
            description = "Apple Inc",
            stockType = "Common Stock"
        )
        val mockAssets = listOf("A" to listOf(asset))
        coEvery { getUSMarketAssetsUseCase(any()) } returns Result.success(mockAssets)
        
        createViewModel()
        viewModel.handleIntent(UsMarketIntent.LoadInitialData(isCrypto = false))
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(1, state.marketAssets.size)
            assertEquals("AAPL", state.marketAssets[0].second[0].symbol)
        }
    }

    @Test
    fun `cuando cambia la query de busqueda, se filtran los assets correctamente`() = runTest {
        val asset1 = MarketAsset("AAPL", 150.0, Currency(EUR), InvestmentType.STOCK, "Apple Inc", stockType = "Common Stock")
        val asset2 = MarketAsset("MSFT", 300.0, Currency(EUR), InvestmentType.STOCK, "Microsoft", stockType = "Common Stock")
        val mockAssets = listOf(
            "A" to listOf(asset1),
            "M" to listOf(asset2)
        )
        coEvery { getUSMarketAssetsUseCase(any()) } returns Result.success(mockAssets)
        
        createViewModel()
        viewModel.handleIntent(UsMarketIntent.LoadInitialData(isCrypto = false))
        
        viewModel.uiState.test {
            awaitItem() // Initial data loaded state
            
            viewModel.handleIntent(UsMarketIntent.SearchQueryChanged("Micro"))
            val state = awaitItem()
            
            assertEquals(1, state.filteredAssets.size)
            assertEquals("MSFT", state.filteredAssets[0].second[0].symbol)
        }
    }

    @Test
    fun `cuando se selecciona un asset, se añade a la lista de seleccionados`() = runTest {
        val asset = MarketAsset("AAPL", 150.0, Currency(EUR), InvestmentType.STOCK, "Apple Inc", stockType = "Common Stock").toUI()
        createViewModel()
        
        viewModel.uiState.test {
            awaitItem() // Initial state
            
            viewModel.handleIntent(UsMarketIntent.SelectAsset(asset))
            val state = awaitItem()
            
            assertTrue(state.assetsToSaveToPortfolio.contains(asset))
        }
    }

    @Test
    fun `cuando se selecciona un asset ya seleccionado, se elimina de la lista`() = runTest {
        val asset = MarketAsset("AAPL", 150.0, Currency(EUR), InvestmentType.STOCK, "Apple Inc", stockType = "Common Stock").toUI()
        createViewModel()
        
        viewModel.uiState.test {
            awaitItem() // Initial
            
            viewModel.handleIntent(UsMarketIntent.SelectAsset(asset))
            awaitItem() // Added
            
            viewModel.handleIntent(UsMarketIntent.SelectAsset(asset))
            val state = awaitItem()
            
            assertFalse(state.assetsToSaveToPortfolio.contains(asset))
        }
    }

    @Test
    fun `cuando se guarda la seleccion, se llama al UseCase por cada asset y se navega hacia atras`() = runTest {
        val asset1 = MarketAsset("AAPL", 150.0, Currency(EUR), InvestmentType.STOCK, "Apple Inc", stockType = "Common Stock").toUI()
        val asset2 = MarketAsset("MSFT", 300.0, Currency(EUR), InvestmentType.STOCK, "Microsoft", stockType = "Common Stock").toUI()
        
        createViewModel()
        viewModel.handleIntent(UsMarketIntent.SelectAsset(asset1))
        viewModel.handleIntent(UsMarketIntent.SelectAsset(asset2))
        
        viewModel.uiState.test {
            // Saltamos los eventos de seleccion hasta que tengamos los 2
            var lastState = awaitItem()
            while (lastState.assetsToSaveToPortfolio.size < 2) {
                lastState = awaitItem()
            }
            
            viewModel.handleIntent(UsMarketIntent.SaveAssetsSelected)
            
            lastState = awaitItem()
            assertTrue(lastState.navigateBack)
            assertEquals(0, lastState.assetsToSaveToPortfolio.size)
            
            coVerify(exactly = 2) { addMarketAssetToPortfolioUseCase(any(), any()) }
        }
    }

    @Test
    fun `al cerrar la card de mercado global, showGlobalMarketsCard pasa a ser false`() = runTest {
        // Primero cargamos datos para que showGlobalMarketsCard sea true
        coEvery { getUSMarketAssetsUseCase(any()) } returns Result.success(emptyList())
        createViewModel()
        viewModel.handleIntent(UsMarketIntent.LoadInitialData(isCrypto = false))
        
        viewModel.uiState.test {
            // Buscamos el estado donde showGlobalMarketsCard es true (tras la carga)
            var state = awaitItem()
            while (!state.showGlobalMarketsCard) {
                state = awaitItem()
            }
            
            viewModel.handleIntent(UsMarketIntent.CloseGlobalMarketCard)
            val finalState = awaitItem()
            
            assertFalse(finalState.showGlobalMarketsCard)
        }
    }
}
