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
class GetUSMarketAssetsUseCaseTest {

    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private val dispatcherProvider = TestDispatcherProvider(mainDispatcherExtension.testDispatcher)

    private val repository = mockk<FinancialRepository>()
    private lateinit var useCase: GetUSMarketAssetsUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetUSMarketAssetsUseCase(repository, dispatcherProvider)
    }

    @Test
    fun `cuando pide stocks, los agrupa por inicial y añade favoritos en la seccion especial`() = runTest {
        // Given
        val asset1 = MarketAsset("AAPL", 150.0, Currency(EUR), InvestmentType.STOCK, "Apple", stockType = "Common")
        val asset2 = MarketAsset("AMZN", 130.0, Currency(EUR), InvestmentType.STOCK, "Amazon", stockType = "Common")
        val asset3 = MarketAsset("MSFT", 300.0, Currency(EUR), InvestmentType.STOCK, "Microsoft", stockType = "Common")
        val asset4 = MarketAsset("Z", 10.0, Currency(EUR), InvestmentType.STOCK, "Z-Corp", stockType = "Common")
        
        coEvery { repository.getStocksSymbols("US") } returns Result.success(listOf(asset1, asset2, asset3, asset4))

        // When
        val result = useCase(isCrypto = false)

        // Then
        assertTrue(result.isSuccess)
        val data = result.getOrNull()!!
        
        // Seccion ★ (Favoritos: AAPL, AMZN, MSFT son favoritos)
        assertEquals("★", data[0].first)
        assertEquals(3, data[0].second.size)
        assertTrue(data[0].second.contains(asset1))
        assertTrue(data[0].second.contains(asset2))
        assertTrue(data[0].second.contains(asset3))
        
        // Seccion A
        assertEquals("A", data[1].first)
        assertEquals(2, data[1].second.size)
        
        // Seccion M
        assertEquals("M", data[2].first)
        assertEquals(1, data[2].second.size)
        
        // Seccion Z
        assertEquals("Z", data[3].first)
        assertEquals(1, data[3].second.size)
    }

    @Test
    fun `cuando pide crypto, llama al repositorio con las monedas permitidas`() = runTest {
        // Given
        val allowedCurrencies = setOf("EUR", "USD", "USDC", "USDT")
        coEvery { repository.getCryptosSymbols(allowedCurrencies) } returns Result.success(emptyList())

        // When
        val result = useCase(isCrypto = true)

        // Then
        assertTrue(result.isSuccess)
        // Verificamos que se llamo a getCryptosSymbols y no a getStocksSymbols
        io.mockk.coVerify { repository.getCryptosSymbols(allowedCurrencies) }
        io.mockk.coVerify(exactly = 0) { repository.getStocksSymbols(any()) }
    }

    @Test
    fun `los assets dentro de cada grupo estan ordenados alfabeticamente`() = runTest {
        // Given
        val assetZ = MarketAsset("Z-Corp", 10.0, Currency(EUR), InvestmentType.STOCK, "Z", stockType = "Common")
        val assetA = MarketAsset("AAPL", 150.0, Currency(EUR), InvestmentType.STOCK, "Apple", stockType = "Common")
        
        coEvery { repository.getStocksSymbols("US") } returns Result.success(listOf(assetZ, assetA))

        // When
        val result = useCase(isCrypto = false)

        // Then
        val data = result.getOrNull()!!
        // Primero A, luego Z (sin contar ★)
        assertEquals("A", data[1].first)
        assertEquals("Z", data[2].first)
    }
}
