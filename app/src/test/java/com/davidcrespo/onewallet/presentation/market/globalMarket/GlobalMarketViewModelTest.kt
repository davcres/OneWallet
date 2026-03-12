package com.davidcrespo.onewallet.presentation.market.globalMarket

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import com.davidcrespo.onewallet.domain.usecase.market.AddMarketAssetToPortfolioUseCase
import com.davidcrespo.onewallet.domain.usecase.market.GetGlobalMarketAssetsUseCase
import com.davidcrespo.onewallet.presentation.models.toUI
import com.davidcrespo.onewallet.util.MainDispatcherExtension
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class GlobalMarketViewModelTest {

    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private val getGlobalMarketAssetsUseCase = mockk<GetGlobalMarketAssetsUseCase>(relaxed = true)
    private val addMarketAssetToPortfolioUseCase = mockk<AddMarketAssetToPortfolioUseCase>(relaxed = true)
    private val savedStateHandle = SavedStateHandle()

    private lateinit var viewModel: GlobalMarketViewModel

    private fun createViewModel() {
        viewModel = GlobalMarketViewModel(
            savedStateHandle,
            getGlobalMarketAssetsUseCase,
            addMarketAssetToPortfolioUseCase
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `al iniciar el ViewModel, carga la query de busqueda desde el SavedStateHandle`() = runTest(mainDispatcherExtension.testDispatcher) {
        savedStateHandle["searchQuery"] = "Tesla"
        createViewModel()
        
        viewModel.uiState.test {
            assertEquals("Tesla", awaitItem().searchQuery)
        }
    }

    @Test
    fun `cuando cambia la query de busqueda, se actualiza el estado`() = runTest(mainDispatcherExtension.testDispatcher) {
        createViewModel()
        
        viewModel.uiState.test {
            awaitItem() // Initial
            
            viewModel.handleIntent(GlobalMarketIntent.OnQueryChange("Nvidia"))
            assertEquals("Nvidia", awaitItem().searchQuery)
        }
    }

    @Test
    fun `cuando se realiza una busqueda por query, se actualiza el estado con los resultados`() = runTest(mainDispatcherExtension.testDispatcher) {
        val asset = MarketAsset(
            symbol = "NVDA",
            price = 500.0,
            currency = Currency(EUR),
            type = InvestmentType.STOCK,
            description = "Nvidia Corp",
            stockType = "Common Stock"
        )
        val mockResults = listOf("N" to listOf(asset))
        coEvery { getGlobalMarketAssetsUseCase(any()) } returns Result.success(mockResults)
        
        createViewModel()
        viewModel.handleIntent(GlobalMarketIntent.SearchByQuery("NVDA"))
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(1, state.marketAssets?.size)
            assertEquals("NVDA", state.marketAssets?.get(0)?.second?.get(0)?.symbol)
        }
    }

    @Test
    fun `cuando falla la busqueda por query, se muestra el error de limite de peticiones`() = runTest(mainDispatcherExtension.testDispatcher) {
        coEvery { getGlobalMarketAssetsUseCase(any()) } returns Result.failure(Exception("API Error"))
        
        createViewModel()
        viewModel.handleIntent(GlobalMarketIntent.SearchByQuery("FAIL"))
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(R.string.global_markets_request_limit, state.error)
            assertTrue(state.marketAssets?.isEmpty() ?: false)
        }
    }

    @Test
    fun `cuando se añade un asset individualmente, se guarda y se navega hacia atras`() = runTest(mainDispatcherExtension.testDispatcher) {
        val asset = MarketAsset("TSLA", 200.0, Currency(EUR), InvestmentType.STOCK, "Tesla", stockType = "Common").toUI()
        createViewModel()
        
        viewModel.handleIntent(GlobalMarketIntent.AddOneAsset(asset))
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.navigateBack)
            assertEquals("", state.searchQuery)
            coVerify { addMarketAssetToPortfolioUseCase(any(), false) }
        }
    }

    @Test
    fun `cuando se selecciona un asset, se añade o quita de la lista de seleccionados`() = runTest(mainDispatcherExtension.testDispatcher) {
        val asset = MarketAsset("AMZN", 100.0, Currency(EUR), InvestmentType.STOCK, "Amazon", stockType = "Common").toUI()
        createViewModel()
        
        viewModel.uiState.test {
            awaitItem() // Initial
            
            // Añadir
            viewModel.handleIntent(GlobalMarketIntent.SelectAsset(asset))
            assertTrue(awaitItem().assetsToSaveToPortfolio.contains(asset))
            
            // Quitar
            viewModel.handleIntent(GlobalMarketIntent.SelectAsset(asset))
            assertFalse(awaitItem().assetsToSaveToPortfolio.contains(asset))
        }
    }

    @Test
    fun `cuando se guardan los assets seleccionados, se llama al UseCase por cada uno y se navega hacia atras`() = runTest(mainDispatcherExtension.testDispatcher) {
        val asset1 = MarketAsset("AAPL", 150.0, Currency(EUR), InvestmentType.STOCK, "Apple", stockType = "Common").toUI()
        val asset2 = MarketAsset("GOOGL", 2800.0, Currency(EUR), InvestmentType.STOCK, "Google", stockType = "Common").toUI()
        
        createViewModel()
        viewModel.handleIntent(GlobalMarketIntent.SelectAsset(asset1))
        viewModel.handleIntent(GlobalMarketIntent.SelectAsset(asset2))
        
        viewModel.uiState.test {
            // Saltamos hasta tener ambos seleccionados
            var lastState = awaitItem()
            while (lastState.assetsToSaveToPortfolio.size < 2) {
                lastState = awaitItem()
            }
            
            viewModel.handleIntent(GlobalMarketIntent.SaveAssetsSelected)
            
            val state = awaitItem()
            assertTrue(state.navigateBack)
            assertEquals(0, state.assetsToSaveToPortfolio.size)
            coVerify(exactly = 2) { addMarketAssetToPortfolioUseCase(any(), false) }
        }
    }

    @Test
    fun `cuando se reintenta la busqueda, se limpia el estado`() = runTest(mainDispatcherExtension.testDispatcher) {
        createViewModel()
        viewModel.handleIntent(GlobalMarketIntent.OnQueryChange("Old Query"))
        
        viewModel.uiState.test {
            awaitItem() // After query change
            
            viewModel.handleIntent(GlobalMarketIntent.RetrySearch)
            val state = awaitItem()
            
            assertEquals("", state.searchQuery)
            assertNull(state.marketAssets)
        }
    }
}
