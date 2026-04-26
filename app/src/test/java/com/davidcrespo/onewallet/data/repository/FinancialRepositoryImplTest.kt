package com.davidcrespo.onewallet.data.repository

import com.davidcrespo.onewallet.data.local.cache.CurrencyCache
import com.davidcrespo.onewallet.data.local.cache.MarketCache
import com.davidcrespo.onewallet.data.local.cache.SymbolCache
import com.davidcrespo.onewallet.data.local.database.portfolio.entities.CurrencyEntity
import com.davidcrespo.onewallet.data.local.database.portfolio.entities.InvestmentEntity
import com.davidcrespo.onewallet.data.remote.alphaVantage.AlphaVantageDataSource
import com.davidcrespo.onewallet.data.remote.binance.BinanceDataSource
import com.davidcrespo.onewallet.data.remote.dto.CurrencyDto
import com.davidcrespo.onewallet.data.remote.dto.InvestmentDto
import com.davidcrespo.onewallet.data.remote.extraEtf.ExtraEtfDataSource
import com.davidcrespo.onewallet.data.remote.finnhub.FinnhubDataSource
import com.davidcrespo.onewallet.data.remote.investing.InvestingDataSource
import com.davidcrespo.onewallet.data.remote.justEtf.JustEtfDataSource
import com.davidcrespo.onewallet.data.remote.marketstack.MarketstackDataSource
import com.davidcrespo.onewallet.data.remote.quefondos.QueFondosDataSource
import com.davidcrespo.onewallet.data.remote.twelveData.TwelveDataDataSource
import com.davidcrespo.onewallet.data.remote.yahooFinance.YahooFinanceDataSource
import com.davidcrespo.onewallet.domain.cache.CachePolicy
import com.davidcrespo.onewallet.domain.logging.Telemetry
import com.davidcrespo.onewallet.domain.model.investment.DataSource
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.MarketType
import com.davidcrespo.onewallet.util.MainDispatcherExtension
import com.davidcrespo.onewallet.util.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class FinancialRepositoryImplTest {

    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private val dispatcherProvider = TestDispatcherProvider(mainDispatcherExtension.testDispatcher)

    private val twelveDataDataSource = mockk<TwelveDataDataSource>(relaxed = true)
    private val finnhubDataSource = mockk<FinnhubDataSource>(relaxed = true)
    private val alphaVantageDataSource = mockk<AlphaVantageDataSource>(relaxed = true)
    private val marketstackDataSource = mockk<MarketstackDataSource>(relaxed = true)
    private val yahooFinanceDataSource = mockk<YahooFinanceDataSource>(relaxed = true)
    private val binanceDataSource = mockk<BinanceDataSource>(relaxed = true)
    private val investingDataSource = mockk<InvestingDataSource>(relaxed = true)
    private val queFondosDataSource = mockk<QueFondosDataSource>(relaxed = true)
    private val justEtfDataSource = mockk<JustEtfDataSource>(relaxed = true)
    private val extraEtfDataSource = mockk<ExtraEtfDataSource>(relaxed = true)
    
    private val symbolCache = mockk<SymbolCache>(relaxed = true)
    private val currencyCache = mockk<CurrencyCache>(relaxed = true)
    private val marketCache = mockk<MarketCache>(relaxed = true)
    private val cachePolicy = mockk<CachePolicy>(relaxed = true)
    private val telemetry = mockk<Telemetry>(relaxed = true)

    private lateinit var repository: FinancialRepositoryImpl

    @BeforeEach
    fun setUp() {
        repository = FinancialRepositoryImpl(
            twelveDataDataSource,
            finnhubDataSource,
            alphaVantageDataSource,
            marketstackDataSource,
            yahooFinanceDataSource,
            binanceDataSource,
            investingDataSource,
            queFondosDataSource,
            justEtfDataSource,
            extraEtfDataSource,
            symbolCache,
            currencyCache,
            marketCache,
            dispatcherProvider,
            cachePolicy,
            telemetry
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `cuando hay cache valida de crypto, devuelve los datos de cache sin llamar a red`() = runTest(mainDispatcherExtension.testDispatcher) {
        // Given
        val symbol = "BTCEUR"
        val cachedEntity = InvestmentEntity(
            symbol = symbol,
            name = "Bitcoin",
            quantity = 1.0,
            price = 60000.0,
            currency = CurrencyEntity(EUR),
            type = InvestmentType.CRYPTO,
            year = 2026,
            month = 3
        )
        every { symbolCache.getCachedInvestmentIfValid(symbol, any()) } returns cachedEntity

        // When
        val result = repository.getInvestmentPrice(symbol, InvestmentType.CRYPTO, "", null, null, null)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(60000.0, result.getOrNull()?.price)
        coVerify(exactly = 0) { binanceDataSource.getCryptoPrice(any()) }
    }

    @Test
    fun `cuando no hay cache de crypto, descarga de red y guarda en cache`() = runTest(mainDispatcherExtension.testDispatcher) {
        // Given
        val symbol = "BTCEUR"
        val dto = InvestmentDto(
            symbol = symbol,
            name = "Bitcoin",
            quantity = 1.0,
            price = 61000.0,
            previousPrice = 60000.0,
            currency = CurrencyDto(EUR),
            type = InvestmentType.CRYPTO,
            year = 2026,
            month = 3
        )
        every { symbolCache.getCachedInvestmentIfValid(any(), any()) } returns null
        coEvery { binanceDataSource.getCryptoPrice(symbol, any()) } returns dto

        // When
        val result = repository.getInvestmentPrice(symbol, InvestmentType.CRYPTO, "Bitcoin", null, null, null)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(61000.0, result.getOrNull()?.price)
        assertEquals("Bitcoin", result.getOrNull()?.name)
        coVerify(exactly = 1) { symbolCache.setCachedInvestment(any()) }
    }

    @Test
    fun `cuando se descarga crypto sin nombre, usa el symbol como nombre`() = runTest(mainDispatcherExtension.testDispatcher) {
        // Given
        val symbol = "BTCEUR"
        // DTO simulating what BinanceDataSource would return if name is empty
        val dto = InvestmentDto(
            symbol = symbol,
            name = symbol, // Result of .ifBlank { symbol }
            quantity = 0.0,
            price = 61000.0,
            previousPrice = 60000.0,
            currency = CurrencyDto(EUR),
            type = InvestmentType.CRYPTO,
            year = 0,
            month = 0
        )
        every { symbolCache.getCachedInvestmentIfValid(any(), any()) } returns null
        coEvery { binanceDataSource.getCryptoPrice(symbol, "") } returns dto

        // When
        val result = repository.getInvestmentPrice(symbol, InvestmentType.CRYPTO, "", null, null, null)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(symbol, result.getOrNull()?.name)
    }

    @Test
    fun `cuando se proporciona preferredApi, intenta esa fuente primero`() = runTest(mainDispatcherExtension.testDispatcher) {
        // Given
        val symbol = "AAPL"
        val preferredSource = DataSource.FINNHUB
        every { symbolCache.getCachedInvestmentIfValid(any(), any()) } returns null
        every { symbolCache.getCachedInvestment(symbol) } returns null

        val dto = InvestmentDto(symbol, "Apple", 0.0, 150.0, 145.0, CurrencyDto("USD"), InvestmentType.STOCK, 2026, 3)
        coEvery { finnhubDataSource.getStockPrice(symbol, any()) } returns dto

        // When
        val result = repository.getInvestmentPrice(symbol, InvestmentType.STOCK, "", null, MarketType.GLOBAL, null, preferredSource)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(preferredSource, result.getOrNull()?.preferredApi)
        coVerify(exactly = 1) { finnhubDataSource.getStockPrice(any(), any()) }
        coVerify(exactly = 0) { yahooFinanceDataSource.getStockPrice(any(), any()) }
    }

    @Test
    fun `cuando preferredApi falla, continua con el resto de la cadena`() = runTest(mainDispatcherExtension.testDispatcher) {
        // Given
        val symbol = "AAPL"
        val preferredSource = DataSource.ALPHA_VANTAGE
        every { symbolCache.getCachedInvestmentIfValid(any(), any()) } returns null
        every { symbolCache.getCachedInvestment(symbol) } returns null

        coEvery { alphaVantageDataSource.getStockPrice(symbol, any(), any()) } returns null
        val dto = InvestmentDto(symbol, "Apple", 0.0, 150.0, 145.0, CurrencyDto("USD"), InvestmentType.STOCK, 2026, 3)
        coEvery { yahooFinanceDataSource.getStockPrice(symbol, any()) } returns dto

        // When
        val result = repository.getInvestmentPrice(symbol, InvestmentType.STOCK, "", null, MarketType.GLOBAL, null, preferredSource)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(DataSource.YAHOO_FINANCE, result.getOrNull()?.preferredApi)
        coVerify(exactly = 1) { alphaVantageDataSource.getStockPrice(any(), any(), any()) }
        coVerify(exactly = 1) { yahooFinanceDataSource.getStockPrice(any(), any()) }
    }

    @Test
    fun `cuando todas las fuentes de Stocks fallan, devuelve Result failure`() = runTest(mainDispatcherExtension.testDispatcher) {
        // Given
        val symbol = "FAIL"
        every { symbolCache.getCachedInvestmentIfValid(any(), any()) } returns null
        
        // Configuramos fallos para toda la cadena GLOBAL
        coEvery { yahooFinanceDataSource.getStockPrice(any(), any()) } returns null
        coEvery { finnhubDataSource.getStockPrice(any(), any()) } throws Exception("Finnhub failed")
        coEvery { alphaVantageDataSource.getStockPrice(any(), any(), any()) } throws Exception("AV failed")
        coEvery { marketstackDataSource.getStockPrice(any(), any()) } throws Exception("Marketstack failed")

        // When
        val result = repository.getInvestmentPrice(symbol, InvestmentType.STOCK, "", null, MarketType.GLOBAL, null)

        // Then
        assertTrue(result.isFailure)
    }
}
