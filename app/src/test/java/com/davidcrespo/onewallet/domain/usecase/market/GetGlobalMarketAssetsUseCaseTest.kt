package com.davidcrespo.onewallet.domain.usecase.market

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import com.davidcrespo.onewallet.util.MainDispatcherRule
import com.davidcrespo.onewallet.util.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetGlobalMarketAssetsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher)

    private val repository = mockk<FinancialRepository>()
    private lateinit var useCase: GetGlobalMarketAssetsUseCase

    @Before
    fun setUp() {
        useCase = GetGlobalMarketAssetsUseCase(repository, dispatcherProvider)
    }

    @Test
    fun `cuando la busqueda es exitosa, devuelve los assets agrupados bajo el simbolo de busqueda`() = runTest {
        // Given
        val query = "AAPL"
        val assets = listOf(
            MarketAsset("AAPL", 150.0, Currency(EUR), InvestmentType.STOCK, "Apple Inc", stockType = "Common Stock")
        )
        coEvery { repository.getStocksSymbolsByQuery(query) } returns Result.success(assets)

        // When
        val result = useCase(query)

        // Then
        assertTrue(result.isSuccess)
        val data = result.getOrNull()!!
        assertEquals(1, data.size)
        assertEquals("⌕", data[0].first)
        assertEquals(assets, data[0].second)
    }

    @Test
    fun `cuando el repositorio falla, el UseCase propaga el error`() = runTest {
        // Given
        val query = "FAIL"
        val exception = Exception("API Error")
        coEvery { repository.getStocksSymbolsByQuery(query) } returns Result.failure(exception)

        // When
        val result = useCase(query)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
