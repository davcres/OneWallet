package com.davidcrespo.onewallet.domain.usecase.market

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import com.davidcrespo.onewallet.util.MainDispatcherExtension
import com.davidcrespo.onewallet.util.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class GetGlobalMarketAssetsUseCaseTest {

    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private val dispatcherProvider = TestDispatcherProvider(mainDispatcherExtension.testDispatcher)

    private val repository = mockk<FinancialRepository>()
    private lateinit var useCase: GetGlobalMarketAssetsUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetGlobalMarketAssetsUseCase(repository, dispatcherProvider)
    }

    @Test
    fun `cuando la busqueda es exitosa, devuelve los assets agrupados bajo el simbolo de busqueda`() = runTest(mainDispatcherExtension.testDispatcher) {
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
    fun `cuando el repositorio falla, el UseCase propaga el error`() = runTest(mainDispatcherExtension.testDispatcher) {
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
